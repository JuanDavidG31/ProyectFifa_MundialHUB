import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})

export class ReportsService {
    private apiReportsUrl = 'http://localhost:8080/api/reports';

    constructor(private http: HttpClient, private authService: AuthService, private router: Router) { }

    savePackageReport(reportData: any): Observable<any> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.post(`${this.apiReportsUrl}/save`, reportData, { headers, responseType: 'text' });
    }

    getUserReports(email: string): Observable<any[]> {
        const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
        return this.http.get<any[]>(`${this.apiReportsUrl}/user?email=${email}`, { headers });
    }
}