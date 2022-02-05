import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'dart:convert' as convert;
class ReceiveView extends StatefulWidget {
  String address;
  String name;
  ReceiveView(this.address, this.name) : super();
  @override
  ReceiveViewState createState() {
    return new ReceiveViewState();
  }
}

class ReceiveViewState extends State<ReceiveView>{
  late String _address;
  late String _name;
  late String addressType;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  @override
  void initState() {
    super.initState();
    _address = widget.address;
    _name = widget.name;
    addressType = "LAT";
  }
  Future<void> getHexAddress() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_hex_address";
    String reply = (await messageChannel.send(msg)) as String;
    print("asset sendMessage get_wallet_address reply = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_wallet_hex_address"){
      var address =  rs["address"];
      var name =  rs["name"];
      this.setState(() {
        _address = address;
        _name = name;
      });
    }
  }

  Future<void> getAddress() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_address";
    String reply = (await messageChannel.send(msg)) as String;
    print("asset sendMessage get_wallet_address reply = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
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
      appBar: AppBar(
          backgroundColor: Colors.indigo,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            S.of(context).receive,
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
      body:

      Container(
        height: MediaQuery.of(context).size.height,
        width: MediaQuery.of(context).size.width,
        decoration: BoxDecoration(
          color: Colors.indigo,
        ),
        child:
            Align(
              alignment: Alignment.topCenter,
              child:
              Container(
                height: 500,
                width: 350,
                margin:EdgeInsets.all(30.0),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(15.0)),
                  color: Colors.white,
                ),
                child:
                  Padding(
                      padding:
                      const EdgeInsets.only(left: 20.0, right: 20.0, top: 8.0, bottom: 5),
                      child:
                      Column(
                          children: <Widget>[
                            SizedBox(
                              height: 30.0,
                            ),
                            Container(
                              height: 40.0,
                              width: 40.0,
                              decoration: BoxDecoration(
                                image:
                                DecorationImage(image: AssetImage("assets/images/main.png"), fit: BoxFit.fill),

                              ),
                            ),
                            SizedBox(
                              height: 20.0,
                            ),
                            Text(
                              _name,
                              style: TextStyle(
                                fontFamily: "Sofia",
                              ),
                            ),
                            SizedBox(
                              height: 20.0,
                            ),

                            QrImage(
                              data: _address,
                              size: 200.0,

                            ),
                            SizedBox(
                              height: 20.0,
                            ),
                            Text(
                              _address,
                              style: TextStyle(
                                fontFamily: "Sofia",
                              ),
                            ),

                              InkWell(
                                onTap: () {
                                  if(addressType =="HEX"){
                                    getAddress();
                                    setState(() {
                                      addressType = "LAT";
                                    });
                                  }else{
                                    getHexAddress();
                                    setState(() {
                                      addressType = "HEX";
                                    });
                                  }

                                },
                                child:
                                  Container(
                                    height: 40.0,
                                    width: 150.0,
                                    child: Center(
                                      child: Text(
                                        addressType =="HEX" ? "LAT Address":"HEX Address",
                                        style:
                                        TextStyle(fontFamily: "Sofia", color: Colors.blue, fontSize: 18.0),
                                      ),
                                    ),
                                  ),
                            ),
                            SizedBox(
                              height: 10.0,
                            ),
                            InkWell(
                              onTap: () {
                                Clipboard.setData(ClipboardData(text: _address));
                                Fluttertoast.showToast(
                                    msg: "复制成功",
                                    toastLength: Toast.LENGTH_SHORT,
                                    gravity: ToastGravity.BOTTOM,
                                    timeInSecForIosWeb: 1,
                                    backgroundColor: Colors.blue,
                                    textColor: Colors.white,
                                    fontSize: 16.0
                                );
                              },
                              child:
                              Container(
                                height: 40.0,
                                width: 150.0,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                  color: Colors.blue,
                                ),

                                child: Center(
                                  child: Text(
                                    S.of(context).copy,
                                    style:
                                    TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                  ),
                                ),
                              ),
                            ),
                          ])
                  ),

            )

          ),
      ),

    );
  }
}