import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:platon_fans/pages/asset/detail.dart';
import 'package:flutter/services.dart';

import 'package:fluttertoast/fluttertoast.dart';
import 'dart:convert' as convert;

import '../messageListView.dart';
import 'homeAsset.dart';
import 'myAsset.dart';
class AssetManagerView extends StatefulWidget {

  @override
  AssetManagerViewState createState() {
    return new AssetManagerViewState();
  }
}

class AssetManagerViewState extends State<AssetManagerView>{
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "资产管理",
            style: TextStyle(
                fontFamily: "Sofia",
                color: Colors.white
            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.chevron_left),
            color: Colors.white,
            onPressed: () {
              Navigator.pop(context,"detail");
            },
          ),
        ),
        backgroundColor: Color(0xFFE2E2E2),
        body: SingleChildScrollView(
        child:Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
            InkWell(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) {
                    return HomeAssetView();
                  }),
                );

              },
              child:
              Container(
                height: 60,
                padding: const EdgeInsets.only(left: 15.0, right: 15.0, ),
                color:Colors.white,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("首页资产管理",style: TextStyle(fontSize: 18),),
                    Icon(Icons.keyboard_arrow_right)
                  ],
                ),
              ),
            ),
          Container(
            height: 1,
            color:  Color(0xFFABABAB).withOpacity(0.3)
          ),
        InkWell(
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) {
                return MyAssetView();
              }),
            );

          },
          child:
          Container(
            height: 60,
            padding: const EdgeInsets.only(left: 15.0, right: 15.0, ),
            color:Colors.white,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("我的所有资产",style: TextStyle(fontSize: 18),),
                Icon(Icons.keyboard_arrow_right)
              ],
            ),
          ),
          ),

          Container(
            height: 2,
            color: Color(0xFFABABAB).withOpacity(0.3)
          ),
          SizedBox(
            height: 20,
          ),
          Container(
            height: 60,
            color: Colors.white,
            alignment: Alignment.centerLeft,
            padding: const EdgeInsets.only(left: 15.0, right: 15.0, ),
            child: Text("热门资产",style: TextStyle(fontSize: 18),),
          ),
          Container(
            color: Colors.white,
              height: MediaQuery.of(context).size.height-360,
              width: MediaQuery.of(context).size.width,
              child:
              ListView.builder(
                //controller: _controller,
                itemCount: 2,
                itemBuilder: (BuildContext context, int position) {

                  return  _list(position, Icons.gamepad, "aaa", "asdf","asdf",1);
                },
              ),
          )

        ],
        )
      )
    );
  }
  Widget _list(int position, IconData icon, String symbol,  String value, String id, int assetType) {
    return
      Container(
          height: 80.0,
          child:
          Column(
              children: <Widget>[
          Padding(
            padding: const EdgeInsets.only(left: 25.0, right: 25.0,bottom: 10 ),
            child:
            InkWell(
              onTap: () {
                // Navigator.of(context).push(PageRouteBuilder(
                //    pageBuilder: (_, __, ___) => new AssetDetailView()));

              },
              child:Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Container(
                            height: 50.0,
                            width: 50.0,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.all(Radius.circular(40.0)),
                              //color: Colors.indigo,
                            ),
                            child: Center(
                              child: Image(
                                width:50,
                                height:50,
                                image:  AssetImage("assets/images/main.png") ,
                              ),
                            ),
                          ),
                          SizedBox(
                            width: 12.0,
                          ),
                          Column(
                            children: <Widget>[
                            Text(
                              symbol,
                              style: TextStyle(
                                  color: Colors.black,
                                  fontFamily: "Popins",
                                 // fontWeight: FontWeight.w600,
                                  fontSize: 20),
                            ),
                              Text(
                                symbol,
                                style: TextStyle(
                                    color: Colors.black54,
                                    fontFamily: "Popins",
                                    // fontWeight: FontWeight.w600,
                                    fontSize: 20),
                              ),
                          ])

                        ],
                      ),
                      Row(
                          children: <Widget>[

                            //isSetting && position != 0?
                            InkWell(
                                onTap: () {
                                  //showDeleteDialog(id);
                                },
                                child:Container(
                                  width: 50,
                                  height: 50,
                                  margin: const EdgeInsets.only( right: 0,),
                                  child: Icon(Icons.add_circle_outline,color:Colors.black ),)
                            )
                          ])

                    ],
                  ),



                ],
              ),
            ),
          ),Container(
            height: 2,
            color: Color(0xFFABABAB).withOpacity(0.3)
            ),
              ])

    );
  }
}