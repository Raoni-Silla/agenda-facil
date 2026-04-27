import { Component } from '@angular/core';
import { ThemeService } from '../../service/theme-service';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';

@Component({
  selector: 'app-servicos',
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './servicos.html',
  styleUrl: './servicos.css',
})
export class Servicos {

  constructor(public themeService : ThemeService){}
  

}
