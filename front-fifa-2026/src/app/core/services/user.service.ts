import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrlUser = 'http://localhost:8080/user';

    constructor(
        private http: HttpClient,
        private router: Router,
        private authService: AuthService
    ) { }


    updateUserAdmin(id: number, userData: any): Observable<any> {
        return this.http.put(`${this.apiUrlUser}/update/${id}`, userData, {
            headers: this.authService.createAuthHeaders()
        });
    }


    rechargeUserCoins(username: string, amount: number): Observable<number> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.put<number>(`${this.apiUrlUser}/recharge?username=${username}&amount=${amount}`, {}, { headers });
    }

    updateProfile(id: number, payload: any): Observable<any> {
        return this.http.put(`${this.apiUrlUser}/updatejson?id=${id}`, payload, {
            headers: this.authService.createAuthHeaders(),
            responseType: 'text' // SpringBoot devuelve un texto, no un JSON estructurado
        });
    }
    getUserByUsername(username: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrlUser}/getbyuser/${username}`, {
            headers: this.authService.createAuthHeaders()
        });
    }
    deleteAccount(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrlUser}/eliminarId/${id}`, {
            headers: this.authService.createAuthHeaders(),
            responseType: 'text'
        });
    }

    actualizarFotoDePerfil(id: number, archivo: File): Observable<any> {
        const formData = new FormData();
        formData.append('id', id.toString());
        formData.append('archivo', archivo, archivo.name);

        const token = this.authService.getToken();
        const headers = token
            ? new HttpHeaders({ Authorization: `Bearer ${token}` })
            : new HttpHeaders();

        return this.http.put(`${this.apiUrlUser}/actualizar-foto-perfil`, formData, { headers });
    }

    updateTutorialStatus(id: number): Observable<any> {
        const formData = new FormData();
        formData.append('id', id.toString());
        const token = this.authService.getToken();
        const headers = token
            ? new HttpHeaders({ Authorization: `Bearer ${token}` })
            : new HttpHeaders();
        return this.http.put(`${this.apiUrlUser}/updateStatusView`, formData, { headers });
    }

    updateStatusConnectFalse(id: number): Observable<any> {
        const formData = new FormData();
        formData.append('id', id.toString());
        const token = this.authService.getToken();
        const headers = token
            ? new HttpHeaders({ Authorization: `Bearer ${token}` })
            : new HttpHeaders();
        return this.http.put(`${this.apiUrlUser}/updateStatusConnectFalse`, formData, { headers });
    }

    updateStatusConnectTrue(id: number): Observable<any> {
        const formData = new FormData();
        formData.append('id', id.toString());
        const token = this.authService.getToken();
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
            headers: this.authService.createAuthHeaders()
        });
    }

    getAllUsers(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrlUser}/showAll`, {
            headers: this.authService.createAuthHeaders()
        });
    }

    checkActiveSupport(): Observable<boolean> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<boolean>(`${this.apiUrlUser}/active-support`, { headers });
    }
}