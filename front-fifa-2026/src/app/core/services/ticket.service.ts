import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';


@Injectable({
    providedIn: 'root'
})

export class TicketsService {
    private apiTicketsUrl = 'https://proyectfifa-mundialhub.onrender.com/api/tickets';
    constructor(private http: HttpClient,
        private router: Router,
        private authService: AuthService) { }

    buyTicket(purchaseData: any): Observable<any> {
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${this.authService.getToken()}`
        });
        return this.http.post(`${this.apiTicketsUrl}/buy`, purchaseData, { headers });
    }

}