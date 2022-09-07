from kivy.app import App

import paho.mqtt.client as mqtt

broker_address = "127.0.0.1"


class Edge2App(App):

    def toggle_light(self, active):
        print("button pressed: publishing a message to the broker: ", str(int(active)))
        # self.client.publish("Light/0", str(int(not active)))

    def toggle_automatic_light(self, active):
        print("Switch pressed: publishing a message to the broker", str(int(active)))
        # self.client.publish("Light/automatic", str(int(not active)))

    def on_start(self):
        def on_message(client, userdata, message):
            print("message received", str(message.payload.decode("utf-8")))
            print("message topic", message.topic)
            userdata['self'].root.ids.temp_lbl.text = str(message.payload.decode("utf-8"))

        parameters = {'self': self}
        # self.client = mqtt.Client(client_id='p1', clean_session=True, userdata = parameters)
        # self.client.connect(broker_address)
        # self.client.on_message = on_message
        # self.client.loop_start()
        print("Subscribing to a topic")
        # self.client.subscribe("home")


Edge2App().run()
