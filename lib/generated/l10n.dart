// GENERATED CODE - DO NOT MODIFY BY HAND
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'intl/messages_all.dart';

// **************************************************************************
// Generator: Flutter Intl IDE plugin
// Made by Localizely
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, lines_longer_than_80_chars
// ignore_for_file: join_return_with_assignment, prefer_final_in_for_each
// ignore_for_file: avoid_redundant_argument_values, avoid_escaping_inner_quotes

class S {
  S();

  static S? _current;

  static S get current {
    assert(_current != null,
        'No instance of S was loaded. Try to initialize the S delegate before accessing S.current.');
    return _current!;
  }

  static const AppLocalizationDelegate delegate = AppLocalizationDelegate();

  static Future<S> load(Locale locale) {
    final name = (locale.countryCode?.isEmpty ?? false)
        ? locale.languageCode
        : locale.toString();
    final localeName = Intl.canonicalizedLocale(name);
    return initializeMessages(localeName).then((_) {
      Intl.defaultLocale = localeName;
      final instance = S();
      S._current = instance;

      return instance;
    });
  }

  static S of(BuildContext context) {
    final instance = S.maybeOf(context);
    assert(instance != null,
        'No instance of S present in the widget tree. Did you add S.delegate in localizationsDelegates?');
    return instance!;
  }

  static S? maybeOf(BuildContext context) {
    return Localizations.of<S>(context, S);
  }

  /// `2`
  String get lang {
    return Intl.message(
      '2',
      name: 'lang',
      desc: '',
      args: [],
    );
  }

  /// `Home`
  String get home {
    return Intl.message(
      'Home',
      name: 'home',
      desc: '',
      args: [],
    );
  }

  /// `Set Language`
  String get settingLanguage {
    return Intl.message(
      'Set Language',
      name: 'settingLanguage',
      desc: '',
      args: [],
    );
  }

  /// `English`
  String get settingLanguageEnglish {
    return Intl.message(
      'English',
      name: 'settingLanguageEnglish',
      desc: '',
      args: [],
    );
  }

  /// `Chinese`
  String get settingLanguageChinese {
    return Intl.message(
      'Chinese',
      name: 'settingLanguageChinese',
      desc: '',
      args: [],
    );
  }

  /// `Settings`
  String get settings {
    return Intl.message(
      'Settings',
      name: 'settings',
      desc: '',
      args: [],
    );
  }

  /// `Me`
  String get me {
    return Intl.message(
      'Me',
      name: 'me',
      desc: '',
      args: [],
    );
  }

  /// `Asset`
  String get asset {
    return Intl.message(
      'Asset',
      name: 'asset',
      desc: '',
      args: [],
    );
  }

  /// `DApp`
  String get dApp {
    return Intl.message(
      'DApp',
      name: 'dApp',
      desc: '',
      args: [],
    );
  }

  /// `Wallet Management`
  String get walletManagement {
    return Intl.message(
      'Wallet Management',
      name: 'walletManagement',
      desc: '',
      args: [],
    );
  }

  /// `Address Book`
  String get addressBook {
    return Intl.message(
      'Address Book',
      name: 'addressBook',
      desc: '',
      args: [],
    );
  }

  /// `About`
  String get about {
    return Intl.message(
      'About',
      name: 'about',
      desc: '',
      args: [],
    );
  }

  /// `Balance`
  String get balance {
    return Intl.message(
      'Balance',
      name: 'balance',
      desc: '',
      args: [],
    );
  }

  /// `Send`
  String get send {
    return Intl.message(
      'Send',
      name: 'send',
      desc: '',
      args: [],
    );
  }

  /// `Receive`
  String get receive {
    return Intl.message(
      'Receive',
      name: 'receive',
      desc: '',
      args: [],
    );
  }

  /// `Transaction History`
  String get transactionHistory {
    return Intl.message(
      'Transaction History',
      name: 'transactionHistory',
      desc: '',
      args: [],
    );
  }

  /// `Copy`
  String get copy {
    return Intl.message(
      'Copy',
      name: 'copy',
      desc: '',
      args: [],
    );
  }

  /// `Wallet Select`
  String get walletSelect {
    return Intl.message(
      'Wallet Select',
      name: 'walletSelect',
      desc: '',
      args: [],
    );
  }

  /// `Add Wallet`
  String get addWallet {
    return Intl.message(
      'Add Wallet',
      name: 'addWallet',
      desc: '',
      args: [],
    );
  }

  /// `Create Wallet`
  String get createWallet {
    return Intl.message(
      'Create Wallet',
      name: 'createWallet',
      desc: '',
      args: [],
    );
  }

  /// `Import Wallet`
  String get importWallet {
    return Intl.message(
      'Import Wallet',
      name: 'importWallet',
      desc: '',
      args: [],
    );
  }

  /// `To Address`
  String get toAddress {
    return Intl.message(
      'To Address',
      name: 'toAddress',
      desc: '',
      args: [],
    );
  }

  /// `From Address`
  String get fromAddress {
    return Intl.message(
      'From Address',
      name: 'fromAddress',
      desc: '',
      args: [],
    );
  }

  /// `Amount`
  String get amount {
    return Intl.message(
      'Amount',
      name: 'amount',
      desc: '',
      args: [],
    );
  }

  /// `Remark`
  String get remark {
    return Intl.message(
      'Remark',
      name: 'remark',
      desc: '',
      args: [],
    );
  }

  /// `Fee`
  String get fee {
    return Intl.message(
      'Fee',
      name: 'fee',
      desc: '',
      args: [],
    );
  }

  /// `Wallet Name`
  String get walletName {
    return Intl.message(
      'Wallet Name',
      name: 'walletName',
      desc: '',
      args: [],
    );
  }

  /// `Password`
  String get password {
    return Intl.message(
      'Password',
      name: 'password',
      desc: '',
      args: [],
    );
  }

  /// `Confirm Password`
  String get confirmPassword {
    return Intl.message(
      'Confirm Password',
      name: 'confirmPassword',
      desc: '',
      args: [],
    );
  }

  /// `Note: Be sure to keep your wallet password in mind, the server will not store your password, you will not be able to retrieve the password if forgetting or losing! `
  String get createWalletNote {
    return Intl.message(
      'Note: Be sure to keep your wallet password in mind, the server will not store your password, you will not be able to retrieve the password if forgetting or losing! ',
      name: 'createWalletNote',
      desc: '',
      args: [],
    );
  }

  /// `Please input wallet name!`
  String get inputWalletName {
    return Intl.message(
      'Please input wallet name!',
      name: 'inputWalletName',
      desc: '',
      args: [],
    );
  }

  /// `Please set password!`
  String get inputPassword {
    return Intl.message(
      'Please set password!',
      name: 'inputPassword',
      desc: '',
      args: [],
    );
  }

  /// `Please enter wallet password!`
  String get inputPassword1 {
    return Intl.message(
      'Please enter wallet password!',
      name: 'inputPassword1',
      desc: '',
      args: [],
    );
  }

  /// `The confirmation password is inconsistent!`
  String get wrongConfirmPassword {
    return Intl.message(
      'The confirmation password is inconsistent!',
      name: 'wrongConfirmPassword',
      desc: '',
      args: [],
    );
  }

  /// `Backup Wallet`
  String get backupWallet {
    return Intl.message(
      'Backup Wallet',
      name: 'backupWallet',
      desc: '',
      args: [],
    );
  }

  /// `Backup prom`
  String get backupProm {
    return Intl.message(
      'Backup prom',
      name: 'backupProm',
      desc: '',
      args: [],
    );
  }

  /// `Getting a mnemonic equals ownership of a wallet asset`
  String get backupPromTxt {
    return Intl.message(
      'Getting a mnemonic equals ownership of a wallet asset',
      name: 'backupPromTxt',
      desc: '',
      args: [],
    );
  }

  /// `Backup mnemonic`
  String get backupMnemonic {
    return Intl.message(
      'Backup mnemonic',
      name: 'backupMnemonic',
      desc: '',
      args: [],
    );
  }

  /// `Use paper and pen to correctly copy mnemonics`
  String get backupMnemonicTxt1 {
    return Intl.message(
      'Use paper and pen to correctly copy mnemonics',
      name: 'backupMnemonicTxt1',
      desc: '',
      args: [],
    );
  }

  /// `If your phone id lost, stolen or damaged, the mnemonic will restore your assets`
  String get backupMnemonicTxt2 {
    return Intl.message(
      'If your phone id lost, stolen or damaged, the mnemonic will restore your assets',
      name: 'backupMnemonicTxt2',
      desc: '',
      args: [],
    );
  }

  /// `Offline storage`
  String get offlineStorage {
    return Intl.message(
      'Offline storage',
      name: 'offlineStorage',
      desc: '',
      args: [],
    );
  }

  /// `Keep it safe to a safe place on the isolated network`
  String get offlineStorageTxt1 {
    return Intl.message(
      'Keep it safe to a safe place on the isolated network',
      name: 'offlineStorageTxt1',
      desc: '',
      args: [],
    );
  }

  /// `Do not share and store mnemonics in a networked environment, such as emails, phone, albums, social, applications`
  String get offlineStorageTxt2 {
    return Intl.message(
      'Do not share and store mnemonics in a networked environment, such as emails, phone, albums, social, applications',
      name: 'offlineStorageTxt2',
      desc: '',
      args: [],
    );
  }

  /// `Delegate`
  String get delegate1 {
    return Intl.message(
      'Delegate',
      name: 'delegate1',
      desc: '',
      args: [],
    );
  }

  /// `Available`
  String get available {
    return Intl.message(
      'Available',
      name: 'available',
      desc: '',
      args: [],
    );
  }

  /// `Locked`
  String get locked {
    return Intl.message(
      'Locked',
      name: 'locked',
      desc: '',
      args: [],
    );
  }

  /// `Version`
  String get version {
    return Intl.message(
      'Version',
      name: 'version',
      desc: '',
      args: [],
    );
  }

  /// `Check Update`
  String get checkUpdate {
    return Intl.message(
      'Check Update',
      name: 'checkUpdate',
      desc: '',
      args: [],
    );
  }

  /// `Address`
  String get address {
    return Intl.message(
      'Address',
      name: 'address',
      desc: '',
      args: [],
    );
  }

  /// `Create PRC20 Token`
  String get dAppPRC20TokenTitle {
    return Intl.message(
      'Create PRC20 Token',
      name: 'dAppPRC20TokenTitle',
      desc: '',
      args: [],
    );
  }

  /// `Delegate`
  String get dAppDelegateTitle {
    return Intl.message(
      'Delegate',
      name: 'dAppDelegateTitle',
      desc: '',
      args: [],
    );
  }

  /// `Cancel`
  String get cancel {
    return Intl.message(
      'Cancel',
      name: 'cancel',
      desc: '',
      args: [],
    );
  }

  /// `Please inter wallet password`
  String get enterPassword {
    return Intl.message(
      'Please inter wallet password',
      name: 'enterPassword',
      desc: '',
      args: [],
    );
  }

  /// `ok`
  String get ok {
    return Intl.message(
      'ok',
      name: 'ok',
      desc: '',
      args: [],
    );
  }

  /// `Import private key`
  String get importPrivateKey {
    return Intl.message(
      'Import private key',
      name: 'importPrivateKey',
      desc: '',
      args: [],
    );
  }

  /// `Import mnemonic`
  String get importMnemonic {
    return Intl.message(
      'Import mnemonic',
      name: 'importMnemonic',
      desc: '',
      args: [],
    );
  }

  /// `Import wallet file`
  String get importFile {
    return Intl.message(
      'Import wallet file',
      name: 'importFile',
      desc: '',
      args: [],
    );
  }

  /// `Private Key`
  String get privateKey {
    return Intl.message(
      'Private Key',
      name: 'privateKey',
      desc: '',
      args: [],
    );
  }

  /// `Mnemonic`
  String get mnemonic {
    return Intl.message(
      'Mnemonic',
      name: 'mnemonic',
      desc: '',
      args: [],
    );
  }

  /// `Wallet File`
  String get walletFile {
    return Intl.message(
      'Wallet File',
      name: 'walletFile',
      desc: '',
      args: [],
    );
  }

  /// `New Asset`
  String get newAsset {
    return Intl.message(
      'New Asset',
      name: 'newAsset',
      desc: '',
      args: [],
    );
  }

  /// `Please inter private key`
  String get enterPrivateKey {
    return Intl.message(
      'Please inter private key',
      name: 'enterPrivateKey',
      desc: '',
      args: [],
    );
  }

  /// `Please inter mnemonic`
  String get enterMnemonic {
    return Intl.message(
      'Please inter mnemonic',
      name: 'enterMnemonic',
      desc: '',
      args: [],
    );
  }

  /// `Please inter wallet file`
  String get enterWalletFile {
    return Intl.message(
      'Please inter wallet file',
      name: 'enterWalletFile',
      desc: '',
      args: [],
    );
  }

  /// `Wallet name already exists`
  String get walletNameExits {
    return Intl.message(
      'Wallet name already exists',
      name: 'walletNameExits',
      desc: '',
      args: [],
    );
  }

  /// `Add`
  String get add {
    return Intl.message(
      'Add',
      name: 'add',
      desc: '',
      args: [],
    );
  }

  /// `Contract address`
  String get contractAddress {
    return Intl.message(
      'Contract address',
      name: 'contractAddress',
      desc: '',
      args: [],
    );
  }

  /// `Please enter wallet address`
  String get enterWalletAddress {
    return Intl.message(
      'Please enter wallet address',
      name: 'enterWalletAddress',
      desc: '',
      args: [],
    );
  }

  /// `Insufficient Balance`
  String get insufficientBalance {
    return Intl.message(
      'Insufficient Balance',
      name: 'insufficientBalance',
      desc: '',
      args: [],
    );
  }

  /// `Transaction Detail`
  String get transactionDetail {
    return Intl.message(
      'Transaction Detail',
      name: 'transactionDetail',
      desc: '',
      args: [],
    );
  }

  /// `Time`
  String get time {
    return Intl.message(
      'Time',
      name: 'time',
      desc: '',
      args: [],
    );
  }

  /// `Transaction Type`
  String get transactionType {
    return Intl.message(
      'Transaction Type',
      name: 'transactionType',
      desc: '',
      args: [],
    );
  }

  /// `Hash`
  String get hash {
    return Intl.message(
      'Hash',
      name: 'hash',
      desc: '',
      args: [],
    );
  }

  /// `Copy Success`
  String get copySuccess {
    return Intl.message(
      'Copy Success',
      name: 'copySuccess',
      desc: '',
      args: [],
    );
  }

  /// ` change password`
  String get changePassword {
    return Intl.message(
      ' change password',
      name: 'changePassword',
      desc: '',
      args: [],
    );
  }

  /// `Old Password`
  String get oldPassword {
    return Intl.message(
      'Old Password',
      name: 'oldPassword',
      desc: '',
      args: [],
    );
  }

  /// `New Password`
  String get newPassword {
    return Intl.message(
      'New Password',
      name: 'newPassword',
      desc: '',
      args: [],
    );
  }
}

class AppLocalizationDelegate extends LocalizationsDelegate<S> {
  const AppLocalizationDelegate();

  List<Locale> get supportedLocales {
    return const <Locale>[
      Locale.fromSubtags(languageCode: 'en'),
      Locale.fromSubtags(languageCode: 'zh'),
    ];
  }

  @override
  bool isSupported(Locale locale) => _isSupported(locale);
  @override
  Future<S> load(Locale locale) => S.load(locale);
  @override
  bool shouldReload(AppLocalizationDelegate old) => false;

  bool _isSupported(Locale locale) {
    for (var supportedLocale in supportedLocales) {
      if (supportedLocale.languageCode == locale.languageCode) {
        return true;
      }
    }
    return false;
  }
}
