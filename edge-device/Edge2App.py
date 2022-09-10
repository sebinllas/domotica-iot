from kivy.app import App

import paho.mqtt.client as mqtt

broker_address = "127.0.0.1"


class Edge2App(App):

    def toggle_light(self, active):
        print("button pressed: publishing a message to the broker: ", str(int(active)))
        self.client.publish("room/light/0", str(int(active)))
        self.client.publish("room/light/automatic", "0")
        self.root.ids.aut_light_sw.active = False

    def toggle_automatic_light(self, active):
        print("Switch pressed: publishing a message to the broker", str(int(active)))
        self.client.publish("room/light/0", "0")
        self.client.publish("room/light/0/automatic", str(int(active)))
        self.root.ids.light_sw.active = False

    def toggle_fan(self, active):
        self.client.publish("room/fan/0", str(int(active)))
        print("Switch pressed: fan message to the broker", str(int(active)))
        self.root.ids.aut_fan_sw.active = False

    def toggle_automatic_fan(self, active):
        self.client.publish("room/fan/0/automatic", str(int(active)))
        print("Switch pressed: fan automatic message to the broker", str(int(active)))
        self.root.ids.fan_sw.active = False

    def toggle_door(self, active):
        self.client.publish("room/door/0", str(int(active)))
        print("Switch pressed: door message to the broker", str(int(active)))

    def on_start(self):
        def on_message(client, userdata, message):
            print("message received", str(message.payload.decode("utf-8")))
            print("message topic", message.topic)
            if message.topic == "home/temp":
                userdata['self'].root.ids.temp_lbl.text = 'Temperature: ' + \
                    str(message.payload.decode('utf-8') + ' °C')
            if message.topic == "home/light/0":
                print("funka")
                userdata['self'].root.ids.light_lbl.text = 'Light: ' + \
                    str(message.payload.decode("utf-8"))
            if message.topic == "home/fan/0":
                userdata['self'].root.ids.fan_lbl.text = 'Fan: ' + \
                    str(message.payload.decode("utf-8"))
            if message.topic == "home/door/0":
                userdata['self'].root.ids.door_lbl.text = 'Door: ' + \
                    str(message.payload.decode("utf-8"))

        parameters = {'self': self}
        self.client = mqtt.Client(
            client_id='p1', clean_session=True, userdata=parameters)
        self.client.connect(broker_address)
        self.client.on_message = on_message
        self.client.loop_start()
        print("Subscribing to a topic")
        self.client.subscribe("home/#")


Edge2App().run()
