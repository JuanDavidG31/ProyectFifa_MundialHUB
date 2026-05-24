import { Component , OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {


  ngOnInit(): void {
    window.scrollTo(0, 0);
  }


  step: number = 1; 

  email: string = '';
  verificationCode: string = '';
  generatedCode: string = '';
  newPassword: string = '';
  showPassword = false;

  isLoading = false;
  errorMsg = '';
  successMsg = '';

  passwordStrength = 0;
  passwordFeedback = '';
  private disallowedSymbols = "<>&\"'/= ";

  constructor(private http: HttpClient, private router: Router) {}

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  checkPasswordStrength(): void {
    const pass = this.newPassword;
    if (!pass) {
      this.passwordStrength = 0;
      this.passwordFeedback = '';
      return;
    }

    const hasDisallowed = [...pass].some(ch => this.disallowedSymbols.includes(ch));
    if (hasDisallowed) {
      this.passwordStrength = 0;
      this.passwordFeedback = 'Caracteres no permitidos';
      return;
    }

    let score = 0;
    if (pass.length >= 8) score++;
    if (/[a-z]/.test(pass) && /[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;

    const hasValidSymbol = [...pass].some(ch =>
      !/[a-zA-Z0-9]/.test(ch) && !this.disallowedSymbols.includes(ch)
    );
    if (hasValidSymbol) score++;

    this.passwordStrength = score;

    if (score < 2) this.passwordFeedback = 'Débil';
    else if (score === 3) this.passwordFeedback = 'Media';
    else if (score === 4) this.passwordFeedback = 'Segura';
  }

  sendCode(): void {
    this.errorMsg = '';
    this.isLoading = true;
    
    this.generatedCode = Math.floor(100000 + Math.random() * 900000).toString();

    const emailBody = `Tu código de verificación es: ${this.generatedCode}. Usa este código para recuperar tu contraseña.`;

    const payload = {
      to: this.email,
      subject: 'Código de Recuperación - Mundial Hub',
      body: emailBody
    };

    this.http.post('https://proyectfifa-mundialhub.onrender.com/api/email/send', payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.isLoading = false;
        this.step = 2; // Pasamos al siguiente paso
        this.successMsg = 'Código enviado. Revisa tu bandeja de entrada.';
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = 'Error al enviar el correo. Verifica que esté bien escrito.';
      }
    });
  }

  verifyCode(): void {
    this.errorMsg = '';
    this.successMsg = '';
    
    if (this.verificationCode === this.generatedCode) {
      this.step = 3; 
    } else {
      this.errorMsg = 'Código incorrecto. Intenta nuevamente.';
    }
  }

  resetPassword(): void {
    this.errorMsg = '';
    this.isLoading = true;

    const url = `https://proyectfifa-mundialhub.onrender.com/auth/reset-password?email=${encodeURIComponent(this.email)}&newPassword=${encodeURIComponent(this.newPassword)}`;
    
    this.http.put(url, {}, { responseType: 'text' }).subscribe({
      next: () => {
        this.isLoading = false;
        alert('¡Tu contraseña ha sido actualizada con éxito! Ya puedes iniciar sesión.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = 'No se pudo actualizar la contraseña. Verifica tu conexión.';
      }
    });
  }
}