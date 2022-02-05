import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';

import 'dart:convert' as convert;

import 'asset/contractTransaction.dart';
import 'delegate/delegate.dart';
class DappView extends StatefulWidget {
  DappViewState createState() => DappViewState();
}

class DappViewState extends State<DappView> {
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();

    receiveMessage();
  }

  Future<void> sendMessage() async {
    Map msg1 = new Map();
    msg1["method"] = "app_create_erc20_token";
    String reply1 = (await messageChannel.send(msg1)) as String;
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    print("reply flutter reply1  = "+ reply1);

    var method =  rs1["method"];
    if(method == "app_create_erc20_token"){
      var address =  rs1["address"];

    }
  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("_basicMessageChannel1 flutter receive dapp = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "app_create_erc20_token"){
        print("_gasLimit = "+ rs["gasLimit"]);
        String gasLimit = rs["gasLimit"];
        String gasPrice = rs["gasPrice"];
        String encodeData = rs["encodeData"];
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) {
            return ContractTransactionView(gasLimit, gasPrice, encodeData);
          }),
        ).then((data){
          if(data == "success"){
            receiveMessage();
            sendMessage();
          }
        });


      }
      return 'Flutter 已收到消息';
    });

  }

  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(
      backgroundColor: Colors.white,
      body: ListView(
        children: <Widget>[
          Container(
            height: 100.0,
            width: MediaQuery.of(context).size.width,
            color: Colors.blue,
            alignment: Alignment.center,
            child:
                Text(
                  "DAPP",
                  style:
                  TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 20.0),
              ),
          ),
          card("assets/images/token.png", S.of(context).dAppPRC20TokenTitle,
              "","createToken"),
         /* card("assets/images/avatar.png", "Create NFT",
              "Create NFT"),*/
          card("assets/images/delegate.png", S.of(context).dAppDelegateTitle,
              "","delegate"),
          SizedBox(
            height: 10.0,
          )
        ],
      ),
    );
  }

  ///
  /// Card under of tab bar
  ///
  Widget card(String img, String title, String subTitle, String type) {
    return InkWell(
        onTap: () {
          if(type == "createToken"){
            sendMessage();
          }else{
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) {
                return DelegateView();
              }),
            );
          }
    },
    child:Padding(
      padding: const EdgeInsets.all(12.0),
      child: Container(
        height: 80.0,

        width: double.infinity,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(10.0)),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                  blurRadius: 10.0,
                  color: Colors.black12.withOpacity(0.1),
                  spreadRadius: 3.0),
            ],
          image: DecorationImage(
            image: AssetImage(img),
            fit: BoxFit.cover,
          ),

        ),

        child:
              Container(
                padding: const EdgeInsets.all(12.0),
                width:MediaQuery.of(context).size.width,
                decoration: BoxDecoration(
                  gradient: LinearGradient(colors: [ Color(0xFFFFFFFF).withOpacity(0.2),Color(0xFFFFFFFF).withOpacity(1)], begin: FractionalOffset(1, 0), end: FractionalOffset(0, 1)),

                  borderRadius: BorderRadius.only(topLeft: Radius.circular(10.0),
                      bottomLeft: Radius.circular(10.0)),
                  color: Colors.white,
                  boxShadow: [
                    BoxShadow(
                        blurRadius: 10.0,
                        color: Colors.black12.withOpacity(0.1),
                        spreadRadius: 3.0),
                  ],


                ),
                child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    title,
                    style: TextStyle(
                      fontFamily: "Popins",
                      color: Colors.black54,
                      fontWeight: FontWeight.w700,
                      fontSize: 20.0,
                    ),
                  ),
                  Text(
                    subTitle,
                    style: TextStyle(
                        color: Colors.black54,
                        fontFamily: "Sans",
                        fontWeight: FontWeight.w600),
                  )
                ],
              ),
        ),
      ),
    )
    );
  }
}
