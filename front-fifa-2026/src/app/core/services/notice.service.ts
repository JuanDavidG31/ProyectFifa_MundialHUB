import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class NoticeService {
    private apiUrl = 'https://proyectfifa-mundialhub.onrender.com/api/notices';

    constructor(private http: HttpClient, private authService: AuthService) { }

    getNotices(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/all`);
    }

    createNotice(notice: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/create`, notice, { headers: this.authService.createAuthHeaders() });
    }

    deleteNotice(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/delete/${id}`, { headers: this.authService.createAuthHeaders() });
    }
}