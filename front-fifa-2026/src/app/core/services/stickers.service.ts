import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable} from 'rxjs';
import { Router } from '@angular/router';
import {AuthService} from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class StickersService {
    private apiStickersUrl = 'https://proyectfifa-mundialhub.onrender.com/api/stickers';
    constructor(private http: HttpClient,
        private router: Router,
        private authService: AuthService) {

        if (this.authService.hasToken()) {
            this.authService.autoLogout();
        }


    }
    getAllStickers(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiStickersUrl}/all`, { headers: this.authService.createAuthHeaders() });
    }

    createSticker(formData: FormData): Observable<any> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.post(`${this.apiStickersUrl}/create`, formData, { headers });
    }

    updateSticker(id: number, formData: FormData): Observable<any> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.put(`${this.apiStickersUrl}/update/${id}`, formData, { headers });
    }

    deleteSticker(id: number): Observable<any> {
        return this.http.delete(`${this.apiStickersUrl}/delete/${id}`, { headers: this.authService.createAuthHeaders() });
    }

}