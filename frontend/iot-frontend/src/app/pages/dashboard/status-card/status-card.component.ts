import { Component, Input } from '@angular/core';
import { MessageService } from 'primeng/api';
import { HomeMessageService } from '../../../services/home-message.service';
// primeNg toggle component

@Component({
  selector: 'ngx-status-card',
  styleUrls: ['./status-card.component.scss'],
  template: `
    <nb-card [ngClass]='{ off: !on }'>
      <div class='manual-control' (click)='toggle()'>
        <div class='icon-container'>
          <div class='icon status-{{ type }}'>
            <ng-content></ng-content>
          </div>
        </div>

        <div class='details'>
          <div class='title h5'>{{ title }}</div>
          <div class='status paragraph-2'>
            {{ on ? 'ON' : 'OFF' }}
          </div>
        </div>
      </div>
      <div class='auto-control' *ngIf='enableAuto'>
        <nb-toggle status='primary' (checkedChange)="toggleAuto()"
          >Automatic</nb-toggle
        >
      </div>
    </nb-card>
  `,
})
export class StatusCardComponent {
  @Input() title: string;
  @Input() type: string;
  @Input() on = true;
  @Input() auto = false;
  @Input() deviceName: string;
  @Input() enableAuto = true;

  constructor(
    private messageService: MessageService,
    private homeMessageService: HomeMessageService,
  ) {}

  checked: boolean = true;

  toggle() {
    if (this.auto) {
      return;
    }
    this.on = !this.on;
    this.messageService.add({
      severity: 'error',
      summary: 'toggle',
      detail: 'Sending message to device',
    });
    this.homeMessageService.sendMessage(
      this.deviceName,
      this.on ? 'on' : 'off',
    );
  }
  toggleAuto() {
    this.auto = !this.auto;
    this.messageService.add({
      severity: 'error',
      summary: 'toggle',
      detail: 'Sending message to device',
    });
    this.homeMessageService.sendMessage(
      this.deviceName,
      this.auto ? 'auto' : 'manual',
    );
  }
}
