import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class FlightsService {
    private apiFlightsUrl = 'https://proyectfifa-mundialhub.onrender.com/api/flights';

    constructor(private http: HttpClient, private authService: AuthService, private router: Router) { }

    getFlightPackage(username: string, startDate: string, endDate: string): Observable<any> {
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${this.authService.getToken()}`
        });

        const params = new HttpParams()
            .set('username', username)
            .set('startDate', startDate)
            .set('endDate', endDate);

        return this.http.get<any>(`${this.apiFlightsUrl}/package`, { headers, params });
    }
}