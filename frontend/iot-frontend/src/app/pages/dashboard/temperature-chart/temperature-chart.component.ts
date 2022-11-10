import { Component, OnDestroy } from '@angular/core';
import { NbThemeService, NbColorHelper } from '@nebular/theme';
import { HomeDataService } from '../../../services/home-data.service';

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


  constructor(
    private homeDataService: HomeDataService,
    private theme: NbThemeService
  ) {
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

  ngOnInit(): void {
    this.getTemperature();
  }

  getTemperature() {
    this.homeDataService
      .getTemperatureData()
      .subscribe((data) => {
        this.tempData(data);
      });
  }

  tempData(data) {
    data.map((x) => {
      this.prom += parseFloat(x.value);
    });
    this.prom = (this.prom / data.length);
    this.data = {
      labels: data.map((item) => item.dateTime),
      datasets: [
        {
          data: data.map((item) => item.value),
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
