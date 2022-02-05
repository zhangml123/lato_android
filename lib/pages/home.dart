import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/assets.dart';
import 'package:platon_fans/pages/createWallet.dart';
import 'package:platon_fans/pages/dapp.dart';
import 'package:platon_fans/pages/me.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import '../CurrentLocale.dart';
class Home extends StatefulWidget {
  @override
  _AppHomeState createState() => _AppHomeState();
}
class _AppHomeState extends State<Home> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  bool showFab = true;
  int _selectedIndex = 0;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  @override
  void initState() {
    super.initState();
    (()async{
        const platform = const MethodChannel('samples.flutter.dev');
        String result= await platform.invokeMethod("wallet_exists");
        print("result   = = = = "+result);
        if(result == "false"){
          Navigator.pushAndRemoveUntil(
            context,
            new MaterialPageRoute(builder: (context) => new CreateWalletView(0)),
            (route) => route == null,
          );

        }
    })();
    _tabController = TabController(vsync: this, initialIndex: _selectedIndex, length: 3);
    _tabController.addListener(() {
      if (_tabController.index == 1) {
        showFab = true;
      } else {
        showFab = false;
      }

    });
    sendMessage();
  }

  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_language";
    String reply = (await messageChannel.send(msg)) as String;
    print("asset sendMessage get_language reply = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_language"){
      if(rs["lang"] == "zh"){
        Provider.of<CurrentLocale>(context, listen: false)
            .setLocale(const Locale('zh', "CH"));
      }else if(rs["lang"] == "en"){
        Provider.of<CurrentLocale>(context, listen: false)
            .setLocale(const Locale('en', "US"));
      }

    }
  }
  Widget callPage(int current) {
    switch (current) {
      case 0:
        return new AssetView();
      case 1:
        return new DappView();
      case 2:
        return new MeView();
      default:
        return Home();
    }
  }
  @override
  Widget build(BuildContext context) {
    //print(S.of(context).settingLanguageChinese);
    return Scaffold(

      body: callPage(_selectedIndex),
      bottomNavigationBar: BottomNavigationBar(
      items: <BottomNavigationBarItem>[
        BottomNavigationBarItem(icon: Icon(Icons.account_balance_wallet), label: S.of(context).asset  ),
        BottomNavigationBarItem(icon: Icon(Icons.widgets), label: S.of(context).dApp  ),
        BottomNavigationBarItem(icon: Icon(Icons.account_box_rounded), label:S.of(context).me ),
      ],
      selectedItemColor:  Colors.indigo,
      currentIndex: _selectedIndex,
      onTap: _onItemTapped,

    ),

    );
  }
  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }
}
