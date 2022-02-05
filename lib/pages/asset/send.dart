import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/asset/sendConfirmView.dart';
import 'package:flutter/services.dart';

import 'package:platon_fans/pages/scan.dart';
import 'dart:convert' as convert;
class SendView extends StatefulWidget {
  String address;
  String id;
  int assetType;
  String symbol;
  SendView(this.address, this.id, this.assetType, this.symbol);
  @override
  SendViewState createState() {
    return new SendViewState();
  }
}

class SendViewState extends State<SendView>{
  String _address = "";
  String _gasLimit = "";
  String _gasPrice = "";
  String _nonce = "";
  String _fee = "";
  String _toAddress = "";
  String _balance = "0";
  String _id = "";
  String _symbol = "";
  int _assetType = 1;
  bool showValueError = false;
  bool showAddressError = false;
  TextEditingController addressController = TextEditingController();
  TextEditingController valueController = TextEditingController();
  TextEditingController markController = TextEditingController();

  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    receiveMessage();
    sendMessage();
    this.addressController.text =  widget.address ;
    this.valueController.text = "";
    _id = widget.id;
    _symbol = widget.symbol;
    _assetType = widget.assetType;
    print("SendViewState address  = " + widget.address);
  }
  Future<void> sendMessage() async {
    Map msg1 = new Map();
    msg1["method"] = "get_estimate_gas";
    String reply1 = (await messageChannel.send(msg1)) as String;
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    print("reply flutter reply1  = "+ reply1);
    Map msg = new Map();
    msg["method"] = "get_wallet_address";
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive2  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_wallet_address"){
      var address =  rs["address"];
      this.setState(() {
        _address = address;
      });
    }

    Map msg2 = new Map();
    msg2["method"] = "get_asset_balance";
    msg2["assetId"] = _id;
    print("reply flutter _id  = "+ _id);
    String reply2 = (await messageChannel.send(msg2)) as String;
    print("reply flutter receive222222  = "+ reply2);
    Map<String, dynamic> rs2 = convert.jsonDecode(reply2);
    var method2 =  rs2["method"];
    if(method2 == "get_asset_balance"){
      this.setState(() {
        _balance = rs2["asset"]["balance"];
        _symbol = rs2["asset"]["symbol"];
      });
    }
  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("_basicMessageChannel1 flutter receive12 = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "get_estimate_gas"){
        this.setState(() {
           _gasLimit = rs["gasLimit"];
           _gasPrice = rs["gasPrice"];
           _nonce = rs["nonce"];
           _fee = rs["fee"];
        });
      }
      return 'Flutter 已收到消息';
    });

  }

  @override
  Widget build(BuildContext context) {
    addressController.addListener(() {
      print('input ${addressController.text}');
      this.setState(() {
        showAddressError = false;
      });
    });

    valueController.addListener(() {
      this.setState(() {
        showValueError=false;
      });

      if(double.parse(valueController.text) > double.parse(_fee )+double.parse(_balance)){
        showValueError = true;
      }
    });
    return new Scaffold(
      resizeToAvoidBottomInset: false,
      appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            S.of(context).send,
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
      body: Container(
        height: MediaQuery.of(context).size.height,
        width: MediaQuery.of(context).size.width,
        decoration: BoxDecoration(
          color: Colors.white,
        ),
        child:
          Padding(
              padding:
              const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 25.0),
              child:Container(
                decoration: BoxDecoration(
                  color: Colors.white,
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                  Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                    children: <Widget>[
                      TextField(
                          controller: addressController,
                          decoration:
                          InputDecoration(
                            labelText: S.of(context).toAddress,
                            errorText: showAddressError ? S.of(context).enterWalletAddress:null,
                            suffixIcon:

                            InkWell(
                            onTap: () {
                              // Navigator.of(context).push(PageRouteBuilder(
                              //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (context) {
                                  return Scan();
                                }),
                              ).then((data){
                                addressController.text = data;
                              });
                            },
                            child:
                            const Icon(
                                IconData(0xe682, fontFamily: 'MyIcons'),
                                size: 20,
                                color: Colors.deepPurpleAccent
                            )),

                      ),
                      ),
                      SizedBox(height: 10.0),

                      TextField(
                        controller: valueController,
                        keyboardType:TextInputType.number,
                        decoration: InputDecoration(
                          labelText: S.of(context).amount,
                          suffixText: S.of(context).balance+ _balance+" " +_symbol,
                          suffixStyle: TextStyle(fontSize: 14),
                          errorText: showValueError ? S.of(context).insufficientBalance:null,
                        ),
                      ),
                      SizedBox(height: 10.0),
                      TextField(
                        controller: markController,
                        decoration: InputDecoration(
                          labelText: S.of(context).remark,//"备注（可选30字以内）",
                        ),
                      ),
                      SizedBox(height: 10.0),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              S.of(context).fee,
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                            Text(
                              _fee,
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),

                          ]
                      ),
                    ]
                  ),


                    InkWell(
                        onTap: () {
                          if(showValueError) return;
                          if(addressController.text ==""){
                            this.setState(() {
                              showAddressError = true;
                            });
                            return;
                          }
                          if(valueController.text == "" || valueController.text == "0"){
                            return;
                          }
                          Navigator.push(
                            context,
                            MaterialPageRoute(builder: (context) {
                              return SendConfirmView(addressController.text, valueController.text, _address, _gasLimit ,_gasPrice, _fee ,_nonce, markController.text, _symbol, _id, _assetType );
                            }),
                          ).then((data){
                            print("result sendview");
                            if(data == "success"){
                              Navigator.pop(context,"success");
                            }
                          });
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
                              S.of(context).send,
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                            ),
                          ),
                        ),
                      ),

                  ],

                ),
              ),
          ),
      ),

    );
  }
}