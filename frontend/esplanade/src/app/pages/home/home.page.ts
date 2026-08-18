import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppNav } from '../../components/nav/app-nav';
import { Tableau } from '../../components/tableau/tableau';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, AppNav, Tableau],
  templateUrl: './home.page.html',
  styleUrls: []
})
export class HomePage {}
