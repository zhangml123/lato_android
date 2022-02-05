import 'package:flutter/material.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';
class AboutView extends StatefulWidget {
  AboutViewState createState() => AboutViewState();
}

class AboutViewState extends State<AboutView> {
  String versionName = "";
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_version";
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_version"){
      this.setState(() {
        versionName =  rs["version"];
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.blue,
        centerTitle: true,
        elevation: 0.5,
        title: Text(
          S.of(context).about,
          style: TextStyle(
            fontFamily: "Sofia",
          ),
        ),
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      backgroundColor: Colors.white,
      body: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: <Widget>[
          Column(
              children: <Widget>[
                SizedBox(
                  height: 40.0,
                ),
                Container(
                  height: 60.0,
                  width: MediaQuery.of(context).size.width,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.all(Radius.circular(40.0)),
                    image:
                    DecorationImage(image: AssetImage("assets/images/logo.png"), fit: BoxFit.contain),
                  ),
                ),
                SizedBox(
                  height: 40.0,
                ),
                Container(
                  height: 60.0,
                  width: MediaQuery.of(context).size.width,
                  alignment: Alignment.center,
                  child: Text("PlatON Wallet",
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 20.0)),
                ),
                SizedBox(
                  height: 40.0,
                ),
                Container(
                  height: 60.0,
                  width: MediaQuery.of(context).size.width,
                  alignment: Alignment.center,
                  child: Text("https://www.platon.network",
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.blue, fontSize: 20.0)),
                ),
              ]
          ),

          Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: <Widget>[
                Container(
                  height: 60.0,
                  width: MediaQuery.of(context).size.width,
                  alignment: Alignment.center,
                  child: Text(
                      S.of(context).version+": v"+versionName,
                      style:
                      TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 20.0)),
                ),
                Container(
                  height: 60.0,
                  width: 200,

                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.all(Radius.circular(40.0)),
                    color: Colors.blue,
                  ),
                  child:
                  Text(

    S.of(context).checkUpdate,
                    style:
                    TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 20.0),
                  ),
                ),
                SizedBox(
                  height: 40.0,
                ),
              ]
          ),


        ],

      ),
    );
  }
}