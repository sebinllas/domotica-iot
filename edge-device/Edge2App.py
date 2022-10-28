from kivy.app import App

import paho.mqtt.client as mqtt

broker_address = "127.0.0.1"
broker_hive_address = "192.168.25.95"


class Edge2App(App):

    def toggle_light(self, active):
        print("button pressed: publishing a message to the broker: ", str(int(active)))
        self.client.publish("room/light/0", str(int(active)))
        self.client.publish("room/light/automatic", "0")
        self.root.ids.switch_automatic.active = False

    def toggle_automatic_light(self, active):
        print("Switch pressed: publishing a message to the broker", str(int(active)))
        self.client.publish("room/light/0", "0")
        self.client.publish("room/light/0/automatic", str(int(active)))
        self.root.ids.switch_light.active = False
        
    def toggle_fan(self, active):
        self.client.publish("room/fan/0", str(int(active)))
        print("Switch pressed: fan message to the broker", str(int(active)))
        
    def toggle_automatic_fan(self, active):
        self.client.publish("room/fan/0/automatic", str(int(active)))
        print("Switch pressed: fan automatic message to the broker", str(int(active)))
        
    def toggle_door(self, active):
        self.client.publish("room/door/0", str(int(active)))
        print("Switch pressed: door message to the broker", str(int(active))) 
        
                
        self.client_hive.publish("home_outbound/home1/temp1", "11")

    def on_start(self):
        def on_message(client, userdata, message):
            print("message received", str(message.payload.decode("utf-8")))
            print("message topic", message.topic)
            if message.topic == "home/temp":
                userdata['self'].root.ids.temp_lbl.text = str(message.payload.decode('utf-8') + ' °C')
                # resend message to backend using mqtt client to hive_broker
                m_temp = str(message.payload.decode('utf-8') + ' °C')
                self.client_hive.publish("home_outbound/home1/temp1", m_temp)
                
            if message.topic == "home/light/0":
                print("funka")
                userdata['self'].root.ids.light_lbl.text = str(message.payload.decode("utf-8"))
                value 
                if str(message.payload.decode("utf-8")) == "0":
                        value = "off"
                else if str(message.payload.decode("utf-8")) == "1":
                        value = "on"
                self.client_hive.publish("home_outbound/home1/light1", value)
                
            if message.topic == "home/fan/0":
                userdata['self'].root.ids.fan_lbl.text = str(message.payload.decode("utf-8"))
                value 
                if str(message.payload.decode("utf-8")) == "0":
                        value = "off"
                else if str(message.payload.decode("utf-8")) == "1":
                        value = "on"
                self.client_hive.publish("home_outbound/home1/fan1",value)
                
            if message.topic == "home/door/0":
                userdata['self'].root.ids.door_lbl.text = str(message.payload.decode("utf-8"))
                value 
                if str(message.payload.decode("utf-8")) == "0":
                        value = "off"
                else if str(message.payload.decode("utf-8")) == "1":
                        value = "on"
                self.client_hive.publish("home_outbound/home1/door1", value)
                
        def on_hive_message(client, userdata, message):
            print("message received on hive", str(message.payload.decode("utf-8")))
            print("message topic", message.topic)
            msg = str(message.payload.decode("utf-8"))
            if message.topic == "home_inbound/home1/light1":
                if msg == 'on':
                        self.client.publish("room/light/0", "1")
                if msg == 'off':
                        self.client.publish("room/light/0", "0")
                if msg == 'auto':
                        self.client.publish("room/light/0/automatic", "1")
                if msg == 'manual':
                        self.client.publish("room/light/0/automatic", "0")
                
            if message.topic == "home_inbound/home1/fan1":
                if msg == 'on':
                        self.client.publish("room/fan/0", "1")
                if msg == 'off':
                        self.client.publish("room/fan/0", "0")
                if msg == 'auto':
                        self.client.publish("room/fan/0/automatic", "1")
                if msg == 'manual':
                        self.client.publish("room/fan/0/automatic", "0")
                
            if message.topic == "home_inbound/home1/door1":
                if msg == 'on':
                        self.client.publish("room/door/0", "1")
                if msg == 'off':
                        self.client.publish("room/door/0", "0")
    

        parameters = {'self': self}
        self.client = mqtt.Client(client_id='p1', clean_session=True, userdata = parameters)
        self.client.connect(broker_address)
        self.client.on_message = on_message
        self.client.loop_start()
        print("Subscribing to a topic")
        self.client.subscribe("home/#")
        
        # config to publish hive_broker
        self.client_hive =  mqtt.Client(client_id='p2', clean_session=True, userdata = parameters)
        self.client_hive.connect(broker_hive_address)
        self.client_hive.on_message = on_hive_message
        self.client_hive.loop_start()
        print("Subscribing to a hive topic")
        self.client_hive.subscribe("home_inbound/#")


Edge2App().run()
