import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/walletList.dart';
import 'package:platon_fans/pages/walletManager.dart';
import 'package:platon_fans/pages/addressList.dart';
import 'package:platon_fans/pages/setting.dart';
import 'package:platon_fans/pages/about.dart';
import 'package:flutter/services.dart';
import 'dart:convert' as convert;
class MeView extends StatefulWidget {
  MeViewState createState() => MeViewState();
}

class MeViewState extends State<MeView>{
  String _address = "";
  String _name = "";
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    //receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_address";
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive  = "+ reply);
    var method =  rs["method"];
    if(method == "get_wallet_address"){
      var address =  rs["address"];
      var name =  rs["name"];
      this.setState(() {
        _address = address;
        _name = name;
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      backgroundColor: Colors.white,
        body: ListView(
          children: <Widget>[
            Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Container(
                      height: 100.0,
                      width: MediaQuery.of(context).size.width,
                      color: Colors.blue,
                      child:
                        Padding(
                          padding:
                           const EdgeInsets.only(left: 10.0, right: 10.0, top: 0),
                          child:
                          Column(
                              children: [
                                SizedBox(
                                  height: 35.0,
                                ),
                                Text(
                                  S.of(context).me,
                                  style:
                                  TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 20.0),
                                ),

                              ]
                          )
                        ),
                    ),
                    Padding(
                      padding:
                      const EdgeInsets.only(left: 20.0, right: 20.0, top: 30,bottom: 30),
                      child:
                    Row(
                      children: [
                        Container(
                          height: 40.0,
                          width: 40.0,
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.all(Radius.circular(40.0)),
                            image:
                            DecorationImage(image: AssetImage("assets/images/avatar.png"), fit: BoxFit.fill),

                          ),
                        ),
                        SizedBox(
                          width: 10.0,
                        ),
                        Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Container(
                                width:MediaQuery.of(context).size.width-160,
                                child: Text(
                                  _name,
                                  softWrap:false ,
                                  overflow: TextOverflow.ellipsis,
                                  style:
                                  TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 20.0),
                                ),
                              ),
                              Container(
                                width:MediaQuery.of(context).size.width-100,
                                child: Text(
                                  _address,
                                  softWrap:false ,
                                  overflow: TextOverflow.ellipsis,
                                  style:
                                  TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                                ),

                              ),

                            ]
                        )
                      ],
                    ),),
                    Column(
                      children: <Widget>[
                       category(
                          txt: S.of(context).walletManagement,
                          icon: Icons.account_balance_wallet_rounded,
                          padding: 20.0,
                          tap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) {
                                //return WalletManagerView();
                                return WalletListView();
                              }),
                            );
                          },
                        ),
                        category(
                          txt: S.of(context).addressBook,
                          icon: Icons.account_box_rounded,
                          padding: 20.0,
                          tap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) {
                                return AddressListView();
                              }),
                            );
                          },
                        ),
                        category(
                          txt: S.of(context).settings,
                          icon: Icons.settings,
                          padding: 20.0,
                          tap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) {
                                return SettingView();
                              }),
                            );
                          },
                        ),
                        category(
                          txt: S.of(context).about,
                          icon: Icons.announcement_rounded,
                          padding: 20.0,
                          tap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) {
                                return AboutView();
                              }),
                            );
                          },
                        ),

                      ],
                    ),

                  ],
                ),
          ]
        )

    );
  }
}
/// Component category class to set list
class category extends StatelessWidget {
  String txt;
  IconData icon;
  GestureTapCallback tap;
  double padding;

  category({required this.txt, required this.icon, required this.tap, required this.padding});

  Widget build(BuildContext context) {
    return InkWell(
      onTap: tap,
      child: Column(
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.only(top: 25.0, left: 30.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                Row(
                  children: <Widget>[
                    Padding(
                      padding: EdgeInsets.only(right: padding),
                      child: Icon(
                        icon,
                        size: 24.0,
                        color: Colors.black54,
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.only(right: 20.0),
                      child: Text(
                        txt,
                        style: TextStyle(
                          fontSize: 14.5,
                          color: Colors.black54,
                          fontWeight: FontWeight.w500,
                          fontFamily: "Sofia",
                        ),
                      ),
                    ),
                  ],
                ),
                Padding(
                  padding: const EdgeInsets.only(right: 20.0),
                  child: Icon(
                    Icons.arrow_forward_ios,
                    color: Colors.black26,
                    size: 15.0,
                  ),
                )
              ],
            ),
          ),
          SizedBox(
            height: 20.0,
          ),
          Divider(
            color: Colors.black12,
          )
        ],
      ),
    );
  }
}