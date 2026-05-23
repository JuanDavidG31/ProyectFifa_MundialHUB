import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class FootballService {
    private apiFootballUrl = 'https://proyectfifa-mundialhub.onrender.com/api/football';
    constructor(private http: HttpClient,
        private router: Router,
        private authService: AuthService) { }

    getUserDashboard(username: string): Observable<any> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any>(`${this.apiFootballUrl}/dashboard?username=${username}`, { headers });
    }
}