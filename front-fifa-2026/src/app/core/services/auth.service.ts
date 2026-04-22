import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';

// 1. <-- NUEVO: Importa el AlbumService (ajusta la ruta según la ubicación real de tu archivo)
import { AlbumService } from '../../features/album/services/album.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private apiUrlUser = 'http://localhost:8080/user';
  private apiStickersUrl = 'http://localhost:8080/api/stickers';
  private apiTicketsUrl = 'http://localhost:8080/api/tickets';
  private apiFootballUrl = 'http://localhost:8080/api/football';
  private apiMatchesUrl = 'http://localhost:8080/api/matches';
  private apiStatsUrl = 'http://localhost:8080/api/stats';
  private tokenKey = 'authToken';
  private roleKey = 'role';
  private timeoutHandler: any;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private albumService: AlbumService // 2. <-- NUEVO: Inyectamos el servicio del álbum
  ) {
    if (this.hasToken()) {
      this.autoLogout();
    }
  }
  // Asegúrate de tener un método de actualización que envíe el objeto completo
  updateUserAdmin(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrlUser}/update/${id}`, userData, {
      headers: this.createAuthHeaders()
    });
  }



  getAllStickers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiStickersUrl}/all`, { headers: this.createAuthHeaders() });
  }

  createSticker(formData: FormData): Observable<any> {
    // Solo enviamos el Token. El navegador pondrá el Content-Type automáticamente para el archivo.
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.post(`${this.apiStickersUrl}/create`, formData, { headers });
  }

  updateSticker(id: number, formData: FormData): Observable<any> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.put(`${this.apiStickersUrl}/update/${id}`, formData, { headers });
  }

  deleteSticker(id: number): Observable<any> {
    return this.http.delete(`${this.apiStickersUrl}/delete/${id}`, { headers: this.createAuthHeaders() });
  }

  login(credentials: LoginRequest): Observable<{ success: boolean, verify?: boolean }> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      map(response => {
        if (response?.token) {


          localStorage.setItem(this.tokenKey, response.token);
          localStorage.setItem(this.roleKey, response.role);
          localStorage.setItem('username', credentials.user);
          if (response.avatar) {
            localStorage.setItem('userAvatar', response.avatar);
          } else {
            localStorage.removeItem('userAvatar');
          }

          if (response.tutorialView == false || response.tutorialView == true) {
            localStorage.setItem('tutorialView', response.tutorialView.toString());
          } else {
            localStorage.removeItem('tutorialView');
          }

          if (response.albumCompleteReward == false || response.albumCompleteReward == true) {
            localStorage.setItem('albumCompleteReward', response.albumCompleteReward.toString());
          } else {
            localStorage.removeItem('albumCompleteReward');
          }

          if (response.countActive == false || response.countActive == true) {
            localStorage.setItem('countActive', response.countActive.toString());
          } else {
            localStorage.removeItem('countActive');
          }

          if (response.verify == false || response.verify == true) {
            localStorage.setItem('verify', response.verify.toString());
          } else {
            localStorage.removeItem('verify');
          }

          if (response.role === 'ADMIN' || response.role === 'USER' || response.role === 'SUPPORT') {
            this.isAuthenticatedSubject.next(true);
            this.autoLogout();


            return { success: true, verify: response.verify };
          }

          this.logout();
        }
        return { success: false };
      })
    );
  }

  sendEmailVerifyCode(user: string): Observable<any> {
    const params = new HttpParams().set('user', user);
    return this.http.post(`${this.apiUrl}/sendEmailVerifyCode`, null, { params });
  }

  getVerificationCode(user: string): Observable<number> {
    const params = new HttpParams().set('user', user);
    return this.http.get<number>(`${this.apiUrl}/codigo`, { params });
  }

  updateVerificationStatus(user: string): Observable<string> {
    const params = new HttpParams().set('user', user);
    return this.http.put(`${this.apiUrl}/updateVerify`, null, { params, responseType: 'text' });
  }

  logout(): void {
    if (this.timeoutHandler) clearTimeout(this.timeoutHandler);

    // 🌟 NUEVO: Si el usuario cierra sesión, destruimos su sala de apuestas
    const currentUsername = localStorage.getItem('username');
    if (currentUsername) {
      this.http.delete(`http://localhost:8080/betting-rooms/force-leave?username=${currentUsername}`).subscribe();
    }

    // Limpiamos el almacenamiento local
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.clear();

    // Limpiamos la memoria del álbum para el próximo usuario
    this.albumService.clearAlbumState();

    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }



  private autoLogout(): void {
    const payload = this.getDecodedToken();
    if (!payload?.exp) return;

    const timeLeft = payload.exp * 1000 - Date.now();

    if (this.timeoutHandler) clearTimeout(this.timeoutHandler);

    if (timeLeft > 0) {
      this.timeoutHandler = setTimeout(() => {
        console.warn('Sesión expirada automáticamente.');
        alert('Tu sesión ha expirado. Por seguridad, ingresa de nuevo.');
        this.logout();
      }, timeLeft);
    } else {
      this.logout();
    }
  }

  register(userData: RegisterRequest, archivo?: File | null): Observable<any> {
    const params = new HttpParams().set('rol', userData.role || 'USER');
    const formData = new FormData();

    const dataBlob = new Blob([JSON.stringify(userData)], { type: 'application/json' });
    formData.append('data', dataBlob);

    if (archivo) {
      formData.append('archivo', archivo, archivo.name);
    }

    return this.http.post(`${this.apiUrl}/register`, formData, { params });
  }


  updateProfile(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrlUser}/updatejson?id=${id}`, payload, {
      headers: this.createAuthHeaders(),
      responseType: 'text' // SpringBoot devuelve un texto, no un JSON estructurado
    });
  }

  sendVerificationEmail(to: string, subject: string, body: string): Observable<any> {
    const payload = { to, subject, body };
    return this.http.post('http://localhost:8080/api/email/send', payload, {
      headers: this.createAuthHeaders(),
      responseType: 'text' // Spring Boot devuelve un String
    });
  }
  getUserByUsername(username: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrlUser}/getbyuser/${username}`, {
      headers: this.createAuthHeaders()
    });
  }
  deleteAccount(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrlUser}/eliminarId/${id}`, {
      headers: this.createAuthHeaders(),
      responseType: 'text'
    });
  }
  actualizarFotoDePerfil(id: number, archivo: File): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('archivo', archivo, archivo.name);

    const token = this.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http.put(`${this.apiUrlUser}/actualizar-foto-perfil`, formData, { headers });
  }

  updateTutorialStatus(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    const token = this.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
    return this.http.put(`${this.apiUrlUser}/updateStatusView`, formData, { headers });
  }

  updateStatusConnectFalse(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    const token = this.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
    return this.http.put(`${this.apiUrlUser}/updateStatusConnectFalse`, formData, { headers });
  }

  updateStatusConnectTrue(id: number): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    const token = this.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
    return this.http.put(`${this.apiUrlUser}/updateStatusConnectTrue`, formData, { headers });
  }

  updateUser(updateData: { id?: number; user?: string; password?: string }): Observable<any> {
    let params = new HttpParams();
    if (updateData.id) params = params.set('id', updateData.id);
    if (updateData.user) params = params.set('newUsername', updateData.user);
    if (updateData.password) params = params.set('newPassword', updateData.password);

    return this.http.put(`${this.apiUrlUser}/update`, null, {
      params,
      headers: this.createAuthHeaders()
    });
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrlUser}/showAll`, {
      headers: this.createAuthHeaders()
    });
  }

  obtenerUrlArchivo(nombreArchivo: string): string {
    return `${this.apiUrl}/archivo/${nombreArchivo}`;
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRole(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  getDecodedToken(): any | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  getUserId(): string | null {
    return this.getDecodedToken()?.sub ?? null;
  }

  private createAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    });
  }

  buyTicket(purchaseData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
    return this.http.post(`${this.apiTicketsUrl}/buy`, purchaseData, { headers });
  }

  getUserDashboard(username: string): Observable<any> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<any>(`${this.apiFootballUrl}/dashboard?username=${username}`, { headers });
  }
  getWcMatches(): Observable<any[]> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<any[]>(`${this.apiMatchesUrl}/wc`, { headers });
  }
  checkActiveSupport(): Observable<boolean> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<boolean>(`${this.apiUrlUser}/active-support`, { headers });
  }

  getTopScorers(): Observable<any[]> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<any[]>(`${this.apiStatsUrl}/scorers`, { headers });
  }

  getTopAssists(): Observable<any[]> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<any[]>(`${this.apiStatsUrl}/assists`, { headers });
  }

  getAllMatches(): Observable<any[]> {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.getToken()}` });
    return this.http.get<any[]>(`${this.apiMatchesUrl}/all`, { headers });
  }


}