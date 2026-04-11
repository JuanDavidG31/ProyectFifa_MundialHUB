import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './settings.html',
  styleUrls: ['./settings.scss'],
})
export class Settings implements OnInit {
  userId: number | null = null;
  currentAvatar: string | null = null;

  formData = {
    name: '',
    email: '',
    user: '',
    password: '',
    coutry: ''
  };
  countries: string[] = [
    'Algeria', 'Argentina', 'Australia',
    'Austria', 'Belgium', 'Bosnia-Herzegovina', 'Brazil', 'Canada', 'Cape Verde Islands', 'Colombia', 'Congo DR', 'Croatia',
    'Curaçao', 'Czechia', 'Ecuador', 'Egypt', 'England', 'France', 'Germany', 'Ghana',
    'Haiti', 'Iran', 'Iraq', 'Ivory Coast', 'Japan', 'Jordan', 'Mexico', 'Morocco',
    'Netherlands', 'New Zealand', 'Norway', 'Panama', 'Paraguay',
    'Portugal', 'Qatar', 'Saudi Arabia', 'Scotland', 'Senegal', 'South Africa', 'South Korea', 'Spain', 'Sweden',
    'Switzerland', 'Tunisia', 'Turkey', 'United States', 'Uruguay', 'Uzbekistan'
  ].sort();
  selectedImage: File | null = null;

  isLoading = false;
  isUploading = false;
  successMsg = '';
  errorMsg = '';

  generatedCode: string = '';
  verificationCode: string = '';
  isCodeSent: boolean = false;
  isSendingCode: boolean = false;
  isDeleting: boolean = false;

  showPassword = false;
  passwordStrength = 0;
  passwordFeedback = '';
  private disallowedSymbols = "<>&\"'/= ";

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentAvatar = localStorage.getItem('userAvatar');
    this.loadUserData();
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  checkPasswordStrength(): void {
    const pass = this.formData.password;
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

  loadUserData(): void {
    const currentUsername = localStorage.getItem('username');
    if (currentUsername) {
      this.authService.getUserByUsername(currentUsername).subscribe({
        next: (me) => {
          if (me) {
            this.userId = me.id;
            this.formData.user = me.user || currentUsername;
            this.formData.name = me.name;
            this.formData.email = me.email;
            this.formData.coutry = me.coutry;
            if (me.avatar) this.currentAvatar = me.avatar;
          }
        },
        error: () => this.showMessage('Error cargando tus datos', 'error')
      });
    }
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedImage = input.files[0];
    }
  }

  actualizarFoto(): void {
    if (!this.selectedImage || !this.userId) return;

    this.isUploading = true;
    this.authService.actualizarFotoDePerfil(this.userId, this.selectedImage).subscribe({
      next: (res) => {
        this.isUploading = false;
        if (res.avatar) {
          this.currentAvatar = res.avatar;
          localStorage.setItem('userAvatar', res.avatar);
        }
        this.showMessage('¡Foto actualizada con éxito!', 'success');
        this.selectedImage = null;
      },
      error: () => {
        this.isUploading = false;
        this.showMessage('Error al subir la foto', 'error');
      }
    });
  }

  enviarCodigoVerificacion(): void {
    if (!this.formData.email) {
      this.showMessage('No hay un correo para enviar el código.', 'error');
      return;
    }

    this.isSendingCode = true;
    this.generatedCode = Math.floor(100000 + Math.random() * 900000).toString();

    const subject = 'Código de Seguridad - Mundial Hub 2026';
    const body = `Hola ${this.formData.name},\n\nHas solicitado cambiar tu contraseña. Tu código de verificación de 6 dígitos es:\n\n${this.generatedCode}\n\nSi no solicitaste este cambio, ignora este mensaje.`;

    this.authService.sendVerificationEmail(this.formData.email, subject, body).subscribe({
      next: () => {
        this.isSendingCode = false;
        this.isCodeSent = true;
        this.showMessage('Código enviado a tu correo.', 'success');
      },
      error: () => {
        this.isSendingCode = false;
        this.showMessage('Error al enviar el correo.', 'error');
      }
    });
  }

  guardarCambios(): void {
    if (!this.userId) return;

    if (this.formData.password) {
      if (!this.isCodeSent) {
        this.showMessage('Debes enviar y verificar el código a tu correo primero.', 'error');
        return;
      }
      if (this.verificationCode !== this.generatedCode) {
        this.showMessage('El código de verificación es incorrecto.', 'error');
        return;
      }
    }

    this.isLoading = true;

    const payload = {
      user: this.formData.user,
      name: this.formData.name,
      email: this.formData.email,
      coutry: this.formData.coutry,
      password: this.formData.password || undefined
    };

    this.authService.updateProfile(this.userId, payload).subscribe({
      next: () => {
        this.isLoading = false;
        const userCambiado = this.formData.user !== localStorage.getItem('username');
        const passCambiada = !!this.formData.password;

        if (userCambiado || passCambiada) {
          alert('Tus credenciales han cambiado. Por seguridad, inicia sesión nuevamente.');
          this.authService.logout();
        } else {
          this.showMessage('¡Perfil actualizado con éxito!', 'success');
        }
      },
      error: () => {
        this.isLoading = false;
        this.showMessage('Error al actualizar el perfil', 'error');
      }
    });
  }

  eliminarCuenta(): void {
    if (!this.userId) return;

    const confirmar = confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción destruirá todas tus láminas y NO se puede deshacer.');

    if (confirmar) {
      this.isDeleting = true;

      this.authService.deleteAccount(this.userId).subscribe({
        next: () => {
          this.isDeleting = false;
          alert('Tu cuenta ha sido eliminada. Lamentamos verte partir.');
          this.authService.logout();
        },
        error: () => {
          this.isDeleting = false;
          this.showMessage('Error al eliminar la cuenta', 'error');
        }
      });
    }
  }

  private showMessage(msg: string, type: 'success' | 'error'): void {
    if (type === 'success') {
      this.successMsg = msg;
      this.errorMsg = '';
    } else {
      this.errorMsg = msg;
      this.successMsg = '';
    }
    setTimeout(() => { this.successMsg = ''; this.errorMsg = ''; }, 4000);
  }
}