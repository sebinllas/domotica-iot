import { Component, NgModule, OnDestroy } from '@angular/core';
import { NbThemeService } from '@nebular/theme';
import {
  Temperature,
  TemperatureHumidityData,
} from '../../../@core/data/temperature-humidity';
import { takeWhile } from 'rxjs/operators';
import { forkJoin, Subscription } from 'rxjs';
// mqtt imports
import { IMqttMessage, MqttService } from 'ngx-mqtt';

@Component({
  selector: 'ngx-temperature',
  styleUrls: ['./temperature.component.scss'],
  templateUrl: './temperature.component.html',
})
export class TemperatureComponent implements OnDestroy {
  private alive = true;
  private subscription: Subscription;

  value: number = null;
  temperatureData: Temperature;
  temperature: number;
  temperatureOff = false;
  temperatureMode = 'cool';

  humidityData: Temperature;
  humidity: number;
  humidityOff = false;
  humidityMode = 'heat';

  theme: any;
  themeSubscription: any;

  constructor(
    private themeService: NbThemeService,
    private temperatureHumidityService: TemperatureHumidityData,
    private _mqttService: MqttService,
  ) {
    this.subscription = this._mqttService
      .observe('web_inbound/home1/temp1')
      .subscribe((message: IMqttMessage) => {
        // tslint:disable-next-line: radix
        this.value = parseInt(message.payload.toString());
        // tslint:disable-next-line:no-console
        console.log(message.payload.toString());
      });

    this.themeService
      .getJsTheme()
      .pipe(takeWhile(() => this.alive))
      .subscribe((config) => {
        this.theme = config.variables.temperature;
      });
  }

  ngOnDestroy() {
    this.alive = false;
  }
}
