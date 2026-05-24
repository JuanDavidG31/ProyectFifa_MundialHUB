import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';

import { AlbumService } from '../../features/album/services/album.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://proyectfifa-mundialhub.onrender.com/auth';
  private tokenKey = 'authToken';
  private roleKey = 'role';
  private timeoutHandler: any;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private albumService: AlbumService
  ) {
    if (this.hasToken()) {
      this.autoLogout();
    }
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

    const currentUsername = localStorage.getItem('username');
    if (currentUsername) {
      this.http.delete(`http://proyectfifa-mundialhub.onrender.com/betting-rooms/force-leave?username=${currentUsername}`).subscribe();
    }

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.clear();

    this.albumService.clearAlbumState();

    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  public autoLogout(): void {
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

  sendVerificationEmail(to: string, subject: string, body: string): Observable<any> {
    const payload = { to, subject, body };
    return this.http.post('http://proyectfifa-mundialhub.onrender.com/api/email/send', payload, {
      headers: this.createAuthHeaders(),
      responseType: 'text' // Spring Boot devuelve un String
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

  public hasToken(): boolean {
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

  public createAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    });
  }

}