import { Injectable, EventEmitter, Output } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class HomeMessageService {
  @Output() disparador: EventEmitter<any> = new EventEmitter();

  mensaje: string;
  private enviarMensajeSubject = new ReplaySubject<string>(1);
  enviarObservable = this.enviarMensajeSubject.asObservable();

  constructor(private http: HttpClient) {}
  HOME_NAME = 'home1';
  sendMessage(deviceName: string, message: string) {
    console.log(`sending request to`);
    return this.http
      .post(
        `http://localhost:8080/home/${this.HOME_NAME}/${deviceName}/${message}`,
        {}
      )
      .subscribe((data) => {
        console.log(data);
      });
  }

  // emiter for the message received from the broker
  emitirMensaje(mensaje: string) {
    this.mensaje = mensaje;
    this.enviarMensajeSubject.next(mensaje);
  }
}
