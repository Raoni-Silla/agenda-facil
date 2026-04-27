import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';

@Component({
  selector: 'app-criar-servico',
  imports: [RouterLink,CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './criar-servico.html',
  styleUrl: './criar-servico.css',
})
export class CriarServico {}
