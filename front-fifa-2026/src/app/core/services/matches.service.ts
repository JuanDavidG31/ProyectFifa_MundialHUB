import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class MatchesService {

    private apiMatchesUrl = 'http://localhost:8080/api/matches';

    constructor(private http: HttpClient,
        private router: Router,
        private authService: AuthService) { }

    getWcMatches(): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiMatchesUrl}/wc`, { headers });
    }

    getAllMatches(): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiMatchesUrl}/all`, { headers });
    }
}