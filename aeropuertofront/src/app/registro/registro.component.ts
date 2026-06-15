import { Component, OnInit } from '@angular/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { ChangeDetectorRef, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { PasajeroService } from '../services/pasajero/pasajero.service';
import { RegistroService } from '../services/registro/registro.service';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

export function mayorDeEdadValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;

    const fechaNacimiento = new Date(control.value);
    const hoy = new Date();

    let edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
    const mes = hoy.getMonth() - fechaNacimiento.getMonth();

    if (mes < 0 || (mes === 0 && hoy.getDate() < fechaNacimiento.getDate())) {
      edad--;
    }

    return edad >= 18 ? null : { menorDeEdad: true };
  };
}

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule, MatDialogModule, MatButtonModule, RouterLink],
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.css']
})
export class RegistroComponent implements OnInit {

  form!: FormGroup;
  usuarioForm!: FormGroup;

  loading = false;
  error: string | null = null;
  mostrarModal: boolean = false;
  pasaporteExiste: boolean = false;
  correoExiste: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private pasajeroService: PasajeroService,
    private registroService: RegistroService
  ) {}

  ngOnInit(): void {

    this.form = this.fb.group({
      pasaporte: ['', [Validators.required, Validators.pattern('^[0-9]{15}$')]],
      nombre: ['', [Validators.required, Validators.pattern("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")]],
      fechaNacimiento: ['', [Validators.required, mayorDeEdadValidator()]],
      nacionalidad: ['', [Validators.required, Validators.pattern("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")]],
      correo: ['', [Validators.required, Validators.email]],
      codigoArea: ['', [Validators.required, Validators.pattern('^[0-9]{1,3}$')]],
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      emergencia: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      direccion: ['', Validators.required]
    });

    this.usuarioForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4), Validators.pattern('^[a-zA-Z0-9._-]+$')]],
      password: ['',[Validators.required,Validators.minLength(6),Validators.pattern(/^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]]
    });
  }

  validarPasaporte() {
    const pasaporte = this.form.get('pasaporte')?.value;
    if (!pasaporte) return;

    this.pasajeroService.existePasaporte(pasaporte).subscribe({
      next: (existe: boolean) => {
        this.pasaporteExiste = existe;
      }
    });
  }

  validarCorreo() {
      const correo = this.form.get('correo')?.value;
      if (!correo) return;

      this.pasajeroService.existeCorreo(correo).subscribe({
        next: (existe: boolean) => {
          this.correoExiste = existe;
        }
      });
    }

  registrar() {
    console.log("Funciona");

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.detectChanges();
      this.mostrarToast("Debe ingresar los campos obligatorios", "snackbar-error");
      return;
    }

    if (this.pasaporteExiste) {
      this.mostrarToast("El número de pasaporte ingresado ya cuenta con usuario.", "snackbar-error");
      return;
    }

    if (this.correoExiste) {
      this.mostrarToast("El correo ya existe", "snackbar-error");
      return;
    }

    this.mostrarModal = true;
  }

  registrarCompleto() {

    if (this.usuarioForm.invalid) {
      this.usuarioForm.markAllAsTouched();
      this.cdr.detectChanges();
      this.mostrarToast("Formato inválido", "snackbar-error");
      return;
    }

    this.mostrarModal = false;

    // 🔥 AQUÍ YA CONFIRMÓ → GUARDAR
    const dto = {
      username: {
        username: this.usuarioForm.value.username,
        password: this.usuarioForm.value.password
      },
      pasajero: {
        noPasaporte: this.form.value.pasaporte,
        nombreCompleto: this.form.value.nombre,
        fechaNacimiento: this.form.value.fechaNacimiento,
        nacionalidad: this.form.value.nacionalidad,
        correo: this.form.value.correo,
        codigoArea: this.form.value.codigoArea,
        telefono: this.form.value.telefono,
        telefonoEmergencia: this.form.value.emergencia,
        direccion: this.form.value.direccion
      }
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '360px',
        disableClose: true
    });

    dialogRef.afterClosed().subscribe((resultado) => {
      if (resultado) {
        this.registroService.registrarCompleto(dto).subscribe({
          next: () => {
            this.mostrarToast("Se ha creado con éxito el usuario.", "snackbar-success");
            this.mostrarModal = false;

            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 1500);
          },
          error: (err: any) => {
            const mensaje = err.error?.message || "Error al registrar";
            this.mostrarToast(mensaje, "snackbar-error");
          }
        });

        return;
      }
      // Usuario eligió NO
        this.ngZone.runOutsideAngular(() => {
          setTimeout(() => {
            this.ngZone.run(() => {
              this.mostrarModal = true;
              this.cdr.detectChanges();

              setTimeout(() => {
                this.mostrarToast(
                  "Se ha cancelado el registro satisfactoriamente",
                  "snackbar-warning"
                );
              }, 50);
            });
          }, 0);
        });
    });
  }

  soloNumeros(event: KeyboardEvent) {
    const tecla = event.key;

    if (
      tecla === 'Backspace' ||
      tecla === 'Tab' ||
      tecla === 'ArrowLeft' ||
      tecla === 'ArrowRight'
    ) {
      return;
    }

    if (!/^[0-9]$/.test(tecla)) {
      event.preventDefault();
    }
  }

  soloLetras(event: KeyboardEvent) {
    const tecla = event.key;

    // Permitir teclas especiales
    if (
      tecla === 'Backspace' ||
      tecla === 'Tab' ||
      tecla === 'ArrowLeft' ||
      tecla === 'ArrowRight' ||
      tecla === 'Delete'
    ) {
      return;
    }

    // Solo letras, espacios, tildes y ñ
    if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]$/.test(tecla)) {
      event.preventDefault();
    }
  }

  mensajeToast: string | null = null;

  mostrarToast(mensaje: string, tipo: string = 'snackbar-error') {
    setTimeout(() => {
      this.snackBar.open(mensaje, '', {
        duration: 3000,
        panelClass: [tipo]
      });
    }, 0);
  }

  campoInvalido(formulario: FormGroup, campo: string): boolean {
    const control = formulario.get(campo);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  limpiar(): void {
    this.form.reset();
    this.usuarioForm.reset();

    this.mostrarModal = false;
    this.pasaporteExiste = false;
    this.correoExiste = false;
    this.error = null;

    this.cdr.detectChanges();
  }

  cancelarRegistro(): void {
    this.usuarioForm.reset();
    this.mostrarModal = false;

    this.mostrarToast(
      'Se ha cancelado el registro satisfactoriamente',
      'snackbar-warning'
    );

    this.cdr.detectChanges();
  }
}
