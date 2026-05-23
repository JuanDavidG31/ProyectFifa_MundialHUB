import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class StatsService {
    private apiStatsUrl = 'https://proyectfifa-mundialhub.onrender.com/api/stats';
    constructor(private http: HttpClient,
        private router: Router,
        private authService: AuthService) { }

    getTopScorers(): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiStatsUrl}/scorers`, { headers });
    }

    getTopAssists(): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiStatsUrl}/assists`, { headers });
    }
}
