import { Component, OnDestroy } from '@angular/core';
import { NbThemeService, NbColorHelper } from '@nebular/theme';
import { Subscription } from 'rxjs';
import { HomeDataService } from '../../../services/home-data.service';
import { IMqttMessage, MqttService } from 'ngx-mqtt';

@Component({
  selector: 'ngx-temperature-chart',
  templateUrl: './temperature-chart.component.html',
  styleUrls: ['./temperature-chart.component.scss'],
})
export class TemperatureChartComponent implements OnDestroy {
  data: any;
  options: any;
  themeSubscription: any;
  prom: any = 0;
  valueTemp: any;
  labels: any = [];
  values: any = [];
  private subscription: Subscription;


  constructor(
    private homeDataService: HomeDataService,
    private theme: NbThemeService,
    private _mqttService: MqttService,
  ) {

    this.subscription = this._mqttService
      .observe('web_inbound/home1/temp1')
      .subscribe((message: IMqttMessage) => {
        // tslint:disable-next-line: radix
        this.valueTemp = parseInt(message.payload.toString());
        this.updateChart(this.valueTemp);
      });

    this.themeSubscription = this.theme.getJsTheme().subscribe((config) => {
      const colors: any = config.variables;
      const chartjs: any = config.variables.chartjs;

      this.data;

      this.options = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              gridLines: {
                display: true,
                color: chartjs.axisLineColor,
              },
              ticks: {
                fontColor: chartjs.textColor,
              },
            },
          ],
          yAxes: [
            {
              gridLines: {
                display: true,
                color: chartjs.axisLineColor,
              },
              ticks: {
                fontColor: chartjs.textColor,
              },
            },
          ],
        },
        legend: {
          labels: {
            fontColor: chartjs.textColor,
          },
        },
      };
    });
  }

  // tslint:disable-next-line: use-lifecycle-interface
  ngOnInit(): void {
    this.getTemperature();
  }

  updateChart(value: any) {
    this.labels.unshift(new Date().toISOString());
    this.values.unshift(value);
    this.values.pop();
    this.labels.pop();

    this.data = {
      labels: this.labels,
      datasets: [
        {
          label: 'Temperature',
          data: this.values,
          backgroundColor: NbColorHelper.hexToRgbA('#5AA454', 0.3),
          borderColor: '#5AA454',
        },
      ],
    };
  }

  getTemperature() {
    this.homeDataService
      .getTemperatureData()
      .subscribe((data) => {
        this.tempData(data);
      });
  }

  tempData(data) {
    this.labels = data.map((item) => item.dateTime);
    this.values = data.map((item) => item.value);
    data.map((x) => {
      this.prom += parseFloat(x.value);
    });
    this.prom = (this.prom / data.length);
    this.data = {
      labels: this.labels,
      datasets: [
        {
          data: this.values,
          label: 'Temperature',
          backgroundColor:  NbColorHelper.hexToRgbA('#5AA454', 0.3),
          borderColor: '#5AA454',
        },
      ],
    };
  }

  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
  }
}
