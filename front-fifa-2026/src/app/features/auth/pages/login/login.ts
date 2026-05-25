import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../core/models/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  ngOnInit(): void {
    window.scrollTo(0, 0);
  }
  credentials: LoginRequest = {
    user: '',
    password: '',

  };
  needsVerification = false;
  verificationCode = '';
  serverCode = '';
  loginError = '';
  isLoading = false;
  showPassword = false;

  constructor(
    public authService: AuthService,
    private router: Router
  ) { }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  login(): void {
    this.loginError = '';
    this.isLoading = true;

    this.authService.login(this.credentials).subscribe({
      next: (res: any) => {
        if (res.success) {
          localStorage.setItem('userName', this.credentials.user);

          if (localStorage.getItem('verify') === 'false') {
            this.needsVerification = true;
            this.isLoading = false;

            this.authService.sendEmailVerifyCode(this.credentials.user).subscribe();

            this.authService.getVerificationCode(this.credentials.user).subscribe({
              next: (code: number) => this.serverCode = code.toString(),
              error: (err) => console.error("Error obteniendo código", err)
            });

          } else {
            this.router.navigate(['/home']);
          }
        } else {
          this.loginError = 'Usuario o contraseña incorrectos';
          this.isLoading = false;
        }
      },
      error: (err) => {
        if (err.status === 401) {
          this.loginError = 'Usuario o contraseña incorrectos';
        } else if (err.status === 0 || err.status >= 500) {
          this.loginError = 'Error de conexión con el servidor';
        } else {
          this.loginError = 'Ocurrió un error inesperado. Intenta nuevamente.';
        }

        this.isLoading = false;
        console.error("Detalle del error de login:", err); 
      }
    });
  }

  verifyUser(): void {
    this.loginError = '';

    if (this.verificationCode.trim() === this.serverCode.trim()) {
      this.isLoading = true;

      this.authService.updateVerificationStatus(this.credentials.user).subscribe({
        next: () => {
          localStorage.setItem('verify', "true");
          this.router.navigate(['/home']);
        },
        error: () => {
          this.loginError = 'Error al verificar. Intenta de nuevo.';
          this.isLoading = false;
        }
      });
    } else {
      this.loginError = 'El código es incorrecto. Intenta de nuevo.';
    }
  }
}