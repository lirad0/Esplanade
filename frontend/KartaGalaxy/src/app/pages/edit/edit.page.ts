import { Component, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AppNav } from '../../components/nav/app-nav';
import { Tableau } from '../../components/tableau/tableau';

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [CommonModule, AppNav, Tableau],
  templateUrl: './edit.page.html',
  styleUrls: []
})
export class EditPage {
  private readonly router = inject(Router);

  @ViewChild('drawer') drawer!: AppNav;

  open() {
    this.drawer?.open();
  }

  back() {
    this.router.navigateByUrl('/');
  }
}
