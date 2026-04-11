import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  activeTab: 'users' | 'stickers' = 'users';

  // ================= VARIABLES USUARIOS =================
  users: any[] = [];
  selectedUser: any = null;
  isLoadingUsers = false;

  // ================= VARIABLES STICKERS =================




  stickers: any[] = [];
  selectedStickerToEdit: any = null;
  isLoadingStickers = false;
  isCreatingSticker = false;
  isUpdatingSticker = false;

  // Variables para la imagen del Nuevo Sticker
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  // Variables para la imagen del Sticker a Editar
  editSelectedFile: File | null = null;
  editImagePreview: string | null = null;

  newSticker = {
    code: '', title: '', sectionId: '', pageTitle: '', rarity: 'Común', type: 'CURRENT_PLAYER', exchangeValue: 10, imageUrl: ''
  };

  // Para crear un nuevo usuario
  isCreatingUser = false;
  registerData: RegisterRequest = {
    user: '', password: '', name: '', personalId: '',
    coutry: '', avatar: '', role: 'USER', email: ''
  };
  imagen: File | null = null;
  userSelectedFile: File | null = null;
  userImagePreview: string | null = null;

  constructor(private authService: AuthService, private http: HttpClient) { }

  ngOnInit() {
    this.loadUsers();
    this.loadStickers();

  }

  loadStickers() {
    this.isLoadingStickers = true;
    this.authService.getAllStickers().subscribe({
      next: (data) => {
        this.stickers = data;
        this.isLoadingStickers = false;
      },
      error: () => this.isLoadingStickers = false
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => this.imagePreview = reader.result as string;
      reader.readAsDataURL(file);
      this.newSticker.imageUrl = ''; // Limpiamos la URL si sube archivo
    }
  }

  onEditFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.editSelectedFile = file;
      const reader = new FileReader();
      reader.onload = () => this.editImagePreview = reader.result as string;
      reader.readAsDataURL(file);
      this.selectedStickerToEdit.imageUrl = ''; // Limpiamos la URL si sube archivo
    }
  }

  createSticker() {
    this.isCreatingSticker = true;
    const formData = new FormData();

    // Armamos el "Paquete" de datos
    formData.append('code', this.newSticker.code);
    formData.append('title', this.newSticker.title);
    formData.append('sectionId', this.newSticker.sectionId);
    formData.append('pageTitle', this.newSticker.pageTitle);
    formData.append('rarity', this.newSticker.rarity);
    formData.append('type', this.newSticker.type);
    formData.append('exchangeValue', this.newSticker.exchangeValue.toString());

    // Agregamos la URL manual si existe, o el Archivo físico si se seleccionó
    if (this.newSticker.imageUrl) formData.append('imageUrl', this.newSticker.imageUrl);
    if (this.selectedFile) formData.append('image', this.selectedFile);

    this.authService.createSticker(formData).subscribe({
      next: () => {
        alert('¡Sticker creado con éxito!');
        this.isCreatingSticker = false;
        // Reiniciamos todo
        this.newSticker = { code: '', title: '', sectionId: '', pageTitle: '', rarity: 'Común', type: 'CURRENT_PLAYER', exchangeValue: 10, imageUrl: '' };
        this.selectedFile = null;
        this.imagePreview = null;
        this.loadStickers();
      },
      error: (err) => {
        alert('Error al crear: ' + (err.error?.error || err.message));
        this.isCreatingSticker = false;
      }
    });
  }

  editSticker(sticker: any) {
    this.selectedStickerToEdit = { ...sticker }; // Clonamos para no editar directamente
  }

  saveStickerEdit() {
    if (!this.selectedStickerToEdit) return;
    this.isUpdatingSticker = true;

    const formData = new FormData();
    formData.append('title', this.selectedStickerToEdit.title);
    formData.append('sectionId', this.selectedStickerToEdit.sectionId);
    formData.append('pageTitle', this.selectedStickerToEdit.pageTitle);
    formData.append('rarity', this.selectedStickerToEdit.rarity);
    formData.append('type', this.selectedStickerToEdit.type || 'CURRENT_PLAYER');
    formData.append('exchangeValue', this.selectedStickerToEdit.exchangeValue.toString());

    if (this.selectedStickerToEdit.imageUrl) formData.append('imageUrl', this.selectedStickerToEdit.imageUrl);
    if (this.editSelectedFile) formData.append('image', this.editSelectedFile);

    this.authService.updateSticker(this.selectedStickerToEdit.id, formData).subscribe({
      next: () => {
        alert('Sticker actualizado con éxito');
        this.isUpdatingSticker = false;
        this.cancelStickerEdit(); // Cerramos modal y limpiamos
        this.loadStickers();
      },
      error: (err) => {
        alert('Error al actualizar: ' + (err.error?.error || err.message));
        this.isUpdatingSticker = false;
      }
    });
  }
  cancelStickerEdit() {
    this.selectedStickerToEdit = null;
    this.editSelectedFile = null;
    this.editImagePreview = null;
  }

  deleteSticker(id: number) {
    if (confirm('⚠️ ¿Estás completamente seguro de eliminar este sticker del juego?')) {
      this.authService.deleteSticker(id).subscribe({
        next: () => {
          alert('Sticker eliminado exitosamente');
          this.loadStickers(); // Refrescamos la tabla
        },
        error: (err) => alert('Error al eliminar: ' + (err.error?.error || err.message))
      });
    }
  }

  // --- LÓGICA DE USUARIOS ---
  loadUsers() {
    this.isLoadingUsers = true;
    this.authService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.isLoadingUsers = false;
      },
      error: (err) => {
        console.error('Error cargando usuarios', err);
        this.isLoadingUsers = false;
      }
    });
  }

  editUser(user: any) {
    // Clonamos el usuario para no editar la tabla directamente hasta guardar
    this.selectedUser = { ...user };
  }

  saveUser() {
    if (!this.selectedUser) return;

    const userToUpdate = { ...this.selectedUser };

    delete userToUpdate.password;

    this.authService.updateProfile(userToUpdate.id, userToUpdate).subscribe({
      next: () => {
        alert('Usuario actualizado con éxito');
        this.selectedUser = null;
        this.loadUsers(); // Recargamos la tabla
      },
      error: (err) => alert('Error al actualizar usuario: ' + (err.error?.error || err.message))
    });
  }

  cancelEdit() {
    this.selectedUser = null;
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.imagen = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.userImagePreview = reader.result as string;
      reader.readAsDataURL(input.files[0]);

    }
    else {
      this.imagen = null;
      this.userImagePreview = null;
    }
  }



  createUser() {
    this.isCreatingUser = true;
    const formData = new FormData();

    // El backend espera estos campos exactos según tu AuthController
    formData.append('name', this.registerData.name);
    formData.append('user', this.registerData.user);
    formData.append('email', this.registerData.email);
    formData.append('personalId', this.registerData.personalId);
    formData.append('coutry', this.registerData.coutry);
    formData.append('password', this.registerData.password);
    formData.append('role', this.registerData.role);

    if (this.userSelectedFile) {
      formData.append('avatar', this.userSelectedFile);
      this.registerData.avatar = formData.get('avatar') as string;

    }

    this.authService.register(this.registerData, this.imagen).subscribe({
      next: () => {
        alert('¡Usuario creado con éxito!');
        this.isCreatingUser = false;
        this.registerData = { name: '', user: '', email: '', personalId: '', coutry: '', password: '', role: 'USER', avatar: '' };
        this.imagen = null;
        this.userImagePreview = null;
        this.userSelectedFile = null;
        this.userImagePreview = null;
        this.loadUsers();
      },
      error: (err) => {
        alert('Error al crear usuario: ' + (err.error?.error || err.message));
        this.isCreatingUser = false;
      }
    });
  }

  deleteUser(id: number) {
    if (confirm('¿Estás completamente seguro de eliminar a este usuario de la base de datos?')) {
      this.authService.deleteAccount(id).subscribe({
        next: () => {
          alert('Usuario eliminado exitosamente');
          this.loadUsers();
        },
        error: (err) => alert('Error al eliminar: ' + (err.error?.error || err.message))
      });
    }
  }


}