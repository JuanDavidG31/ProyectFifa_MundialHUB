import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service'
import { Observable } from 'rxjs';
import { take, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.isAuthenticated$.pipe(
      take(1),
      map(isAuthenticated => {
        console.log('isAuthenticated en AuthGuard:', isAuthenticated);
        
        if (isAuthenticated) {
          const expectedRole = route.data['expectedRole']; 
          
          if (expectedRole) {
            const currentRole = this.authService.getRole(); 
            
            if (currentRole !== expectedRole) {
              console.warn('Acceso denegado: No tienes permisos de administrador.');
              this.router.navigate(['/home']);
              return false;
            }
          }
          return true;
          
        } else {
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
          return false;
        }
      })
    );
  }
}