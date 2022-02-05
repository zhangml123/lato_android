import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';

import 'package:platon_fans/pages/createWalletNext.dart';
import 'package:platon_fans/pages/importWallet.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:platon_fans/pages/home.dart';
class CreateWalletView extends StatefulWidget {
  int nodeId;
  CreateWalletView(this.nodeId) : super();
  CreateWalletViewState createState() => CreateWalletViewState();
}
class CreateWalletViewState extends State<CreateWalletView> {
  String name = "" ;
  String password = "";
  String re_password = "";
  Color buttonColor =  Colors.blue;
  bool submitted = false;
  late int _nodeId;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());

  void initState() {
    super.initState();

    this._nodeId = widget.nodeId;
  }

  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///

    return Scaffold(

        resizeToAvoidBottomInset: false,
      backgroundColor: Colors.white,
      appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            S.of(context).addWallet ,
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
      body:Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Container(
              width:  MediaQuery.of(context).size.width,
              height: 300,
              child: Image(
                width:40,
                height:40,
                image:  AssetImage("assets/images/main.png") ,
              ),
            ),
            Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              ElevatedButton(
                style: ButtonStyle(
                    textStyle: MaterialStateProperty.all(
                        TextStyle(fontSize: 18,)),
                    padding: MaterialStateProperty.all(EdgeInsets.only(left:100,right: 100,top:10,bottom: 10)),
                    shape: MaterialStateProperty.all(
                        StadiumBorder(
                            side: BorderSide(
                              //设置 界面效果
                              style: BorderStyle.solid,
                              color: Colors.blue,
                            )
                        )
                    )

                ),
                child: Text(
                  S.of(context).createWallet ,
                  style: TextStyle(color: Colors.white),
                ),

                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) {
                      return CreateWalletNextView(_nodeId);
                    }),
                  );
                },
              ),
              SizedBox(height: 10,),
              ElevatedButton(
                style: ButtonStyle(
                    textStyle: MaterialStateProperty.all(
                        TextStyle(fontSize: 18,)),
                    padding: MaterialStateProperty.all(EdgeInsets.only(left:100,right: 100,top:10,bottom: 10)),
                    shape: MaterialStateProperty.all(
                        StadiumBorder(
                            side: BorderSide(
                              //设置 界面效果
                              style: BorderStyle.solid,
                              color: Colors.blue,
                            )
                        )
                    )

                ),
                child: Text(
                  S.of(context).importWallet ,
                  style: TextStyle(color: Colors.white),
                ),

                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) {
                      return ImportWalletView(_nodeId);
                    }),
                  );
                },
              ),
              SizedBox(height: 30,),
            ])
          ])
    );
  }
}