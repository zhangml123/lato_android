import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/languageSet.dart';

import 'asset/receive.dart';

class SettingView extends StatefulWidget {
  SettingViewState createState() => SettingViewState();
}

class SettingViewState extends State<SettingView> {
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            S.of(context).settings,
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

                          /*card("assets/images/avatar.png", "节点地址",
                              "http://example.com"),*/
                          card("assets/images/avatar.png", S.of(context).settingLanguage,
                              "简体中文"),

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
  Widget card(String img, String title, String subTitle) {
    return Padding(
      padding: const EdgeInsets.only(left: 20.0, right: 20.0, top:15),
      child:



      InkWell(
      onTap: () {
      // Navigator.of(context).push(PageRouteBuilder(
      //    pageBuilder: (_, __, ___) => new AssetDetailView()));
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) {
            return LanguageSetView();
          }),
        );
      },
      child: Container(
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
              child:  Icon(Icons.chevron_right_outlined),
            ),
          ],
        ),
      ),
      )
    );
  }
}
