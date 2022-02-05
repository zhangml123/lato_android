import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:provider/provider.dart';

import 'dart:convert' as convert;

import 'package:flutter/services.dart';
import '../CurrentLocale.dart';
import 'asset/receive.dart';

class LanguageSetView extends StatefulWidget {
  LanguageSetViewState createState() => LanguageSetViewState();
}

class LanguageSetViewState extends State<LanguageSetView> {
  String current = "";
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  Future<void> sendMessage(String lang) async {
    Map msg = new Map();
    msg["method"] = "set_language";
    msg["lang"] = lang;
    String reply = (await messageChannel.send(msg)) as String;
    print("asset sendMessage get_language reply = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "set_language"){


    }
  }
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    ///
    current = S.of(context).lang ;
    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
              S.of(context).settingLanguage
            ,
            style: TextStyle(
              fontFamily: "Sofia",

            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.chevron_left),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
        ),
        body:ListView(
            children: <Widget>[
              Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    Column(
                        children: <Widget>[

                          InkWell(
                              onTap: () {
                                // Navigator.of(context).push(PageRouteBuilder(
                                //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                                print("InkWell1111  zh");
                                Provider.of<CurrentLocale>(context, listen: false)
                                    .setLocale(const Locale('zh', "CH"));
                                this.setState(() {
                                  current = "1";
                                });
                                sendMessage("zh");
                              },
                              child:
                              card("assets/images/avatar.png", "中文",
                              "http://example.com",current == "1"),

                          ),
                          InkWell(
                            onTap: () {
                              // Navigator.of(context).push(PageRouteBuilder(
                              //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                              print("InkWell11111  en ");
                              Provider.of<CurrentLocale>(context, listen: false)
                                  .setLocale(const Locale('en', "US"));
                              this.setState(() {
                                current = "2";
                              });
                              sendMessage("en");
                            },
                            child:
                            card("assets/images/avatar.png", "English",
                                "简体中文",current == "2"),
                          ),

                          SizedBox(
                            height: 10.0,
                          ),

                        ]),


                  ]
              )])

    );
  }

  ///
  /// Card under of tab bar
  ///
  Widget card(String img, String title, String subTitle, bool showIcon) {
    return Padding(
        padding: const EdgeInsets.only(left: 20.0, right: 20.0, top:15),
        child:
        Container(
            height: 60.0,
            width: double.infinity,
            decoration: BoxDecoration(
                borderRadius: BorderRadius.all(Radius.circular(5.0)),
                color: Colors.white,
                boxShadow: [
                  BoxShadow(
                      blurRadius: 5.0,
                      color: Colors.black12.withOpacity(0.1),
                      spreadRadius: 3.0),
                ]),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.only(left: 30.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(
                        title,
                        style: TextStyle(
                          fontFamily: "Popins",
                          color: Colors.black87,
                          fontSize: 18.0,
                        ),
                      ),

                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(right: 20.0),
                  child: showIcon ?  Icon(Icons.check_circle ) : SizedBox(),
                ),
              ],
            ),
        )
    );
  }
}
