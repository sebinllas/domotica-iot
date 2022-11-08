import { Component, OnInit } from "@angular/core";
import { HomeDataService } from "../../../services/home-data.service";

@Component({
  selector: "ngx-temperature-chart",
  templateUrl: "./temperature-chart.component.html",
  styleUrls: ["./temperature-chart.component.scss"],
})
export class TemperatureChartComponent implements OnInit {
  constructor(private homeDataService: HomeDataService) {}

  ngOnInit(): void {
    //this.homeDataService.getTemperature().subscribe((data) => {
  }
}
