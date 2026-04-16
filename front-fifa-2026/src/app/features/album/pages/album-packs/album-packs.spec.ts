import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumPacks } from './album-packs';

describe('AlbumPacks', () => {
  let component: AlbumPacks;
  let fixture: ComponentFixture<AlbumPacks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumPacks]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AlbumPacks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
