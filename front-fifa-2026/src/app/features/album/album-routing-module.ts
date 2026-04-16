import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlbumOverview } from './pages/album-overview/album-overview';
import { AlbumPacks } from './pages/album-packs/album-packs';
import { AlbumSection } from './pages/album-section/album-section';
import { AlbumExchange } from './pages/album-exchange/album-exchange';
import { AlbumHistory } from './pages/album-history/album-history';

const routes: Routes = [
  { path: '', component: AlbumOverview },
  { path: 'packs', component: AlbumPacks },
  { path: 'section/:id', component: AlbumSection },
  { path: 'exchange', component: AlbumExchange }, 
  { path: 'history', component: AlbumHistory }    
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AlbumRoutingModule { }