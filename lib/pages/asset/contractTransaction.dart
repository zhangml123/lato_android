import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:convert' as convert;

import 'package:fluttertoast/fluttertoast.dart';
import 'package:platon_fans/generated/l10n.dart';
class ContractTransactionView extends StatefulWidget {
  String gasLimit;
  String gasPrice;
  String encodeData;
  ContractTransactionView(this.gasLimit, this.gasPrice, this.encodeData);

  @override
  ContractTransactionViewState createState() {
    return new ContractTransactionViewState();
  }
}

class ContractTransactionViewState extends State<ContractTransactionView>{
  String _gasLimit = "";
  String _gasPrice = "";
  String _encodeData = "";
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  @override
  void initState() {
    super.initState();
    _gasLimit = widget.gasLimit;
    _gasPrice = widget.gasPrice;
    _encodeData = widget.encodeData;
    receiveMessage();
  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("_basicMessageChannel2 flutter receive2 = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "send_transfer_transaction"){
        var status =  rs["status"];
        print("reply flutter status  = "+ status);
        if(status == "success"){
          Navigator.pop(context,"success");
        }

      }
      return 'Flutter 已收到消息';
    });
  }

  Future<void> checkPassword(password) async {
    Map msg = new Map();
    msg["method"] = "check_password";
    msg["password"] = password;
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "check_password"){
      var status =  rs["status"];
      print("reply flutter status  = "+ status);
      if(status == "success"){
        submit(password);
      }else{
        Fluttertoast.showToast(
            msg: "密码错误!",
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.CENTER,
            timeInSecForIosWeb: 1,
            backgroundColor: Colors.deepOrangeAccent,
            textColor: Colors.white,
            fontSize: 16.0
        );
      }

    }
  }
  Future<void> submit(password) async {
    Map msg = new Map();
    msg["method"]   = "send_contract_transaction";
    msg["gasPrice"]   = _gasPrice;
    msg["gasLimit"]   = _gasLimit;
    msg["encodeData"]   = _encodeData;
    msg["password"] = password;
    print("reply flutter gasPrice  = "+ _gasPrice);

    print("reply flutter _gasLimit  = "+ _gasLimit);
    // print("reply flutter receive  valueController.text= "+ valueController.text);
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "send_transfer_transaction"){

    }
  }


  void simpleDialog(TextEditingController valueController) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(S.of(context).inputPassword1,
              style: TextStyle(
                  fontFamily: "Sofia",
                  fontWeight: FontWeight.w700,
                  fontSize: 18.0)),
          content:TextField(
            controller: valueController,

            obscureText: true,
            decoration: InputDecoration(
              labelText: S.of(context).password,
            ),
          ),
          actions: <Widget>[
            FlatButton(
              child:  Text(S.of(context).cancel, style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                valueController.text = "";
                Navigator.of(context).pop();
              },
            ),
            FlatButton(
              child:  Text(S.of(context).ok, style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                String password = valueController.text;
                valueController.text = "";
                checkPassword(password);
                Navigator.of(context).pop();
              },
            )
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    TextEditingController valueController = TextEditingController();
    valueController.addListener(() {
      print('input ${valueController.text}');

    });
    return new Scaffold(

      resizeToAvoidBottomInset: false,
      appBar: AppBar(
        backgroundColor: Colors.blue,
        centerTitle: true,
        elevation: 0.5,
        title: Text(
          "部署合约",
          style: TextStyle(
            fontFamily: "Sofia",
          ),
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
          const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 25.0),
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
                      Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              "发送交易",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 20.0),
                            ),
                          ]
                      ),
                      SizedBox(height: 10.0),
                      Container(
                        alignment: Alignment.topLeft,
                        child: Text(
                          "发送地址:",
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),
                      ),

                      SizedBox(height: 5.0),
                      Container(
                        alignment: Alignment.topLeft,
                        child: Text(
                          "",
                          // maxLines: 5,
                          overflow: TextOverflow.fade,
                          softWrap: true,
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),

                      ),


                      SizedBox(height: 20.0),
                      Container(
                        alignment: Alignment.topLeft,
                        child: Text(
                          "接收地址:",
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),
                      ),
                      SizedBox(height: 5.0),

                      SizedBox(height: 20.0),
                      Row(
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "网络:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "PlatON TestNet:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 20.0),
                      Row(
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "数量:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "0 LAT",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 20.0),
                      Row(
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "手续费:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                               " LAT",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Icon(
                              Icons.arrow_drop_down,
                              color: Colors.black54,
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "encodeData",

                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                         Container(
                            width: MediaQuery.of(context).size.width,
                            height: 200,
                            alignment: Alignment.topLeft,
                            child:SingleChildScrollView(
                              child: Text(
                              _encodeData,
                              maxLines: 100,
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        )


                    ]

                ),

                Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      InkWell(
                        onTap: () {
                          // Navigator.of(context).push(PageRouteBuilder(
                          //    pageBuilder: (_, __, ___) => new AssetDetailView()));

                          Navigator.pop(context);
                        },
                        child:
                        Container(
                          height: 40.0,
                          width: 150.0,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.all(Radius.circular(15.0)),
                            color: Colors.red,
                          ),
                          child: Center(
                            child: Text(
                              "取消",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                            ),
                          ),
                        ),
                      ),
                      InkWell(
                        onTap: () {
                          simpleDialog( valueController);
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
                              "发送交易",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                            ),
                          ),
                        ),
                      ),
                    ])

              ],

            ),
          ),
        ),
      ),

    );
  }
}
