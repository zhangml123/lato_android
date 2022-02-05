import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';

class AddressListView extends StatefulWidget {
  AddressListViewState createState() => AddressListViewState();
}

class AddressListViewState extends State<AddressListView> {
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

          S.of(context).address,
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
      body: ListView(
        children: <Widget>[

         /* card("assets/images/avatar.png", "HDYE8E7S55D9S887D9S8DSDFASD",
              "Create PRC20Token"),
          card("assets/images/avatar.png", "HDYE8E7S55D9S887D9S8DSDFASD",
              "Create NFT"),
          card("assets/images/avatar.png", "HDYE8E7S55D9S887D9S8DSDFASD",
              "Delegate"),*/
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
  Widget card(String img, String title, String subTitle) {
    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Container(
        height: 60.0,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(10.0)),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                  blurRadius: 10.0,
                  color: Colors.black12.withOpacity(0.1),
                  spreadRadius: 3.0),
            ]),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(right: 5.0),
              child: Container(
                width: 40.0,
                height: 40.0,
                decoration: BoxDecoration(
                    image: DecorationImage(
                        image: AssetImage(img), fit: BoxFit.fill)),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 30.0),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: <Widget>[
                    Container(
                      width: 200,
                      alignment: Alignment.topLeft,
                      child: Text(
                        "AW33F3FFSDF23SDWE23333322WEWEWFADFWAWWDWW",
                        // maxLines: 5,
                        overflow: TextOverflow.fade,
                        softWrap: true,
                        style:
                        TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                      ),

                    ),
                  ]

              ),
            ),

          ],
        ),
      ),
    );
  }
}
