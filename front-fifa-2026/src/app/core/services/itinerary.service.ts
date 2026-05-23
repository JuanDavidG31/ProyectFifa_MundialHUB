import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class ItineraryService {
    private apiItineraryUrl = 'https://proyectfifa-mundialhub.onrender.com/api/itinerary';

    constructor(private http: HttpClient, private authService: AuthService, private router: Router) { }

    getUserItinerary(email: string): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiItineraryUrl}/${email}`, { headers });
    }

    saveItineraryEvents(events: any[]): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.post<any[]>(`${this.apiItineraryUrl}/save`, events, { headers });
    }

    deleteItineraryEvent(id: number): Observable<any> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.delete(`${this.apiItineraryUrl}/delete/${id}`, { headers });
    }
}