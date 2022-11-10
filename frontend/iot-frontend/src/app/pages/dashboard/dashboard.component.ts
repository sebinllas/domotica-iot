import { Component, OnDestroy } from '@angular/core';
import { NbThemeService } from '@nebular/theme';
import { takeWhile } from 'rxjs/operators';
import { SolarData } from '../../@core/data/solar';
import { IMqttMessage, MqttService } from 'ngx-mqtt';
import { Subscription } from 'rxjs';
import { HomeDataService } from '../../services/home-data.service';

interface CardSettings {
  title: string;
  iconClass: string;
  type: string;
  deviceName: string;
  on: boolean;
  auto: boolean;
  enableAuto: boolean;
}

@Component({
  selector: 'ngx-dashboard',
  styleUrls: ['./dashboard.component.scss'],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnDestroy {
  private alive = true;
  private subscription: Subscription;

  solarValue: number;
  lightCard: CardSettings = {
    title: 'Light',
    iconClass: 'nb-lightbulb',
    type: 'primary',
    deviceName: 'light1',
    on: true,
    auto: false,
    enableAuto: true,
  };
  fanCard: CardSettings = {
    title: 'Fan',
    iconClass: 'nb-snowy-circled',
    type: 'success',
    deviceName: 'fan1',
    on: true,
    auto: false,
    enableAuto: true,
  };
  doorCard: CardSettings = {
    title: 'Door',
    iconClass: 'nb-home',
    type: 'info',
    deviceName: 'door1',
    on: true,
    auto: false,
    enableAuto: false,
  };

  statusCards: string;

  commonStatusCardsSet: CardSettings[] = [
    this.lightCard,
    this.fanCard,
    this.doorCard,
  ];

  updateCards() {
    this.commonStatusCardsSet = [this.lightCard, this.fanCard, this.doorCard];
  }
  statusCardsByThemes: {
    default: CardSettings[];
    cosmic: CardSettings[];
    corporate: CardSettings[];
    dark: CardSettings[];
  } = {
    default: this.commonStatusCardsSet,
    cosmic: this.commonStatusCardsSet,
    corporate: [
      {
        ...this.lightCard,
        type: 'warning',
      },
      {
        ...this.fanCard,
        type: 'primary',
      },
      {
        ...this.doorCard,
        type: 'danger',
      },
    ],
    dark: this.commonStatusCardsSet,
  };

  constructor(
    private themeService: NbThemeService,
    private solarService: SolarData,
    private _mqttService: MqttService,
    private homeDataService: HomeDataService,
  ) {
    this.subscription = this._mqttService
      .observe('web_inbound/#')
      .subscribe((message: IMqttMessage) => {
        const topic = message.topic;
        const payload = message.payload.toString();

        const action = {
          'web_inbound/home1/light1': () => {
            this.lightCard = { ...this.lightCard, on: payload === 'on' };
          },
          'web_inbound/home1/fan1': () => {
            this.fanCard = {
              ...this.fanCard,
              on: payload === 'on',
            };
          },
          'web_inbound/home1/door1': () => {
            this.doorCard = {
              on: payload === 'on',
              ...this.doorCard,
            };
          },
        };
        if (topic in action) {
          action[topic]();
          this.updateCards();
        }
      });

    this.themeService
      .getJsTheme()
      .pipe(takeWhile(() => this.alive))
      .subscribe((theme) => {
        this.statusCards = this.statusCardsByThemes[theme.name];
      });

    this.solarService
      .getSolarData()
      .pipe(takeWhile(() => this.alive))
      .subscribe((data) => {
        this.solarValue = data;
      });
  }

  ngOnDestroy() {
    this.alive = false;
  }

  // tslint:disable-next-line: use-lifecycle-interface
  ngOnInit() {
    this.homeDataService.getTemperatureData();
    this.homeDataService.getLightData().subscribe((data) => {
      // tslint:disable-next-line: no-console
      console.log(data[0]);
    });
  }
}
