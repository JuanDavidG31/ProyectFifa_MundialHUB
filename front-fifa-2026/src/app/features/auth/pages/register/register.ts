import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';
import { RegisterRequest } from '../../../../core/models/auth.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register implements OnInit {
  ngOnInit(): void {
    window.scrollTo(0, 0);
  }
  registerData: RegisterRequest = {
    user: '', password: '', name: '', personalId: '',
    coutry: '', avatar: '', role: 'USER', email: ''
  };

  registrationError = '';
  registrationSuccess = '';
  imagen: File | null = null;
  isLoading = false;

  showPassword = false;
  passwordStrength = 0;
  passwordFeedback = '';
  private disallowedSymbols = "<>&\"'/= ";
  countries: string[] = [
    'Algeria', 'Argentina', 'Australia',
    'Austria', 'Belgium', 'Bosnia-Herzegovina', 'Brazil', 'Canada', 'Cape Verde Islands', 'Colombia', 'Congo DR', 'Croatia',
    'Curaçao', 'Czechia', 'Ecuador', 'Egypt', 'England', 'France', 'Germany', 'Ghana',
    'Haiti', 'Iran', 'Iraq', 'Ivory Coast', 'Japan', 'Jordan', 'Mexico', 'Morocco',
    'Netherlands', 'New Zealand', 'Norway', 'Panama', 'Paraguay',
    'Portugal', 'Qatar', 'Saudi Arabia', 'Scotland', 'Senegal', 'South Africa', 'South Korea', 'Spain', 'Sweden',
    'Switzerland', 'Tunisia', 'Turkey', 'United States', 'Uruguay', 'Uzbekistan'
  ].sort();

  constructor(private authService: AuthService, private router: Router) { }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.imagen = input.files[0];
    }
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onPasswordChange(): void {
    const pass = this.registerData.password;
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

  crearCuenta(): void {
    this.isLoading = true;
    this.authService.register(this.registerData, this.imagen).subscribe({
      next: () => {
        this.registrationSuccess = '¡Registro completado!';
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.isLoading = false;
        this.registrationError = err.error?.message || 'Error al registrar';
      }
    });
  }
}