# 読書管理アプリ「My Library」
My Libraryは読書記録を一元的に管理するアプリです。<br>
今自分が何の本を読んでいるか管理、また感想等自由に書き込めます。そして、読み終えた本は本棚に保管され、ユーザーは今まで読んだ本を後から振り返ることができます。<br>
作成した背景としては、書籍の勉強からアプリ演習で技術を獲得したいと考えた際、せっかく作るなら何か役立つものが良いと考えました。
そこで、自分が今まで読んだ本を単純に覚えていなかったこと、また友人におすすめの本を聞いた際、何を読んだか忘れてしまったと言っていたことから、このアプリを作成することを決めました。<br>
<br>
Spring Boot × Thymeleafで作成したSSR方式です。<br>
まだ完成しておらず、なるべく早いデプロイを目指して頑張っています。※基本機能は実装済み<br>
また、SPAの勉強として、完成後はRESTAPIに作り変えることを検討しています。

# 主な機能一覧
### 1. **ユーザーログイン機能**
- ユーザーログインを実装することで自分だけの読書リストを作成できます。
- remember-me機能を実装し、煩わしいログイン処理から解放されます。
### 2. **読書中の本を管理(CRUD)**
- 今何の本を読んでいるかを記録し、メモ等自由に書き込めます。
- 内容を自由にカスタマイズでき、条件指定による検索も可能です。
### 3. **本棚に読み終えた本を保管(CRUD)**
- 読み終えた本は本棚に保管されます、これで自分が今までどんな本を読んだか忘れません。
- もちろん後から内容を編集することも可能です。
- 検索条件を指定し本を詳細に検索することもできます。

<!-- # 詳細画面 -->

# 使用技術
<table>
  <!-- ヘッダ -->
  <tr>
    <td>Lang</td>
    <td>Framework</td>
    <td>DB</td>
    <td>Env</td>
  </tr>
  <!-- ボディ -->
  <tr>
    <td>
      <img src="https://img.shields.io/badge/Java-v21-gray?labelColor=4682b4">
      <br>
      <img src="https://img.shields.io/badge/-HTML5-333.svg?logo=html5">
      <br>
      <img src="https://img.shields.io/badge/-CSS3-1572B6.svg?logo=css3">
      <br>
      <img src="https://img.shields.io/badge/-JavaScript-276DC3.svg?logo=javascript">
    </td>
    <td>
      <img src="https://img.shields.io/badge/-Spring-EEE.svg?logo=spring">
      <img src="https://img.shields.io/badge/Spring_Boot-v3.2.2-gray?logo=springboot&labelColor=EEE">
      <img src="https://img.shields.io/badge/Spring_Security-v6.2.1-gray?logo=springsecurity&labelColor=EEE">
      <br>
      <img src="https://img.shields.io/badge/Bootstrap-v5.3.0-gray?logo=bootstrap&logoColor=white&labelColor=7952B3">
    </td>
    <td>
      <img src="https://img.shields.io/badge/PostgreSQL-v16-gray?logo=postgresql&logoColor=white&labelColor=336791">
    </td>
    <td>
      <img src="https://img.shields.io/badge/-Eclipse_IDE-2C2255.svg?logo=eclipseide">
      <br>
      <img src="https://img.shields.io/badge/Maven-red">
      <br>
      <img src="https://img.shields.io/badge/-Windows_11-0078D4.svg?logo=windows11">
    </td>
  </tr>
</table>

# DB設計
### - ER図
<img width="1440" alt="ER" src="https://github.com/daiki9898/reading_app/assets/158798056/7f1e1ba1-e960-4fc8-9682-b0f911aeab65">

### - テーブル定義書 user関係
![table-user](https://github.com/daiki9898/reading_app/assets/158798056/7d495566-b47a-4530-8868-a66dddde9e46)

### - book関係
![table-book](https://github.com/daiki9898/reading_app/assets/158798056/dc37f6b2-d6cf-42bb-8058-baeee518fde0)

# セキュリティ対策
### 1. **パスワードのハッシュ化**
- これは当たり前ですが、DBには平文ではなくハッシュ化されたパスワードを保存するようにしています。
### 2. **SQLインジェクション、 XSS対策**
- まずSQLインジェクション対策として、引数箇所をプレースホルダで指定し変数をバインドすることで、特殊文字をエスケープしただの値として扱います。
- またXSS対策として、フォームから入力された文字においても、同様に特殊文字をエスケープし単なる文章として表示しています。
### 3. **CSRFトークンを使用**
- デフォルトのCSRF対策を有効化し、ログイン処理から適用されるようにカスタマイズしました。<br>
具体的には、POSTリクエスト時にCSRFトークンを送信し検証するようにしています。
### 4. **CORS設定**
- 異なるオリジンでのリソース共有を防ぐため、ローカルホストからのリクエストしか受け付けないようにしています。<br>
これはXSSやCSRF対策になります。
### 5. **Remember-Meトークンはデータベースに保存**
- 単純なユーザー名とパスワードのハッシュでは脆弱性があり、アプリを再起動すると値が変わってしまい、ログインを有効にするためKeyを一定にしなければいけません。もちろんHTTPS通信でCookieをセキュアに管理する必要がありますが、仮にハッシュ値が漏洩すればレインボーテーブル攻撃のリスクがあります。
- それに対して、トークンをリポジトリーで管理する方法ではその心配はありません。この場合Remember-Meトークンは、シリーズ（主キー）とトークンで構成されています。トークンは毎回生成されますがDBに記録されているため、アプリを再起動してもログインは継続します。
- また、Spring Securityではデフォルトでセッション固定保護が有効になっているため、セッションハイジャッキングへの対策もされています。<br>
> [!CAUTION]
> まだまだ対策は不十分です、勉強中であるため間違っている可能性は大いにあります。

# 苦労した点
### 1. **画像の保存&表示**
- 画像のsrc(resources下のパス)をDBに保存し、エンコードして表示するという方法を採用しました。
- 画像ファイルの受け取りは、SpringのMultipartFileを使用します。
### 2. **JOIN結果をDTOにセット**
- 一般的なCRUD操作だけならSpring Data JPAを使えば、クエリをかかずに簡単に操作ができます。しかし、JOIN結果を取得するには適していませんでした。
- ネット上のあらゆるサイトを検索し試しました。そこでは、Repositoryのメソッドに@Queryで、データを受け取るDTOクラスをFQCNで指定し、インスタンス化して値をセットすると書いてありました。しかし、いくらやっても上手くいきませんでした。
- 最終的に、JdbcTemplateでDTOとJOIN結果のmappingを行う方法を採用しました。
<!-- [この本](https://www.amazon.co.jp/%E3%83%97%E3%83%AD%E3%81%AB%E3%81%AA%E3%82%8B%E3%81%9F%E3%82%81%E3%81%AESpring%E5%85%A5%E9%96%80%E3%83%BC%E3%83%BC%E3%82%BC%E3%83%AD%E3%81%8B%E3%82%89%E3%81%AE%E9%96%8B%E7%99%BA%E5%8A%9B%E9%A4%8A%E6%88%90%E8%AC%9B%E5%BA%A7-%E5%9C%9F%E5%B2%90-%E5%AD%9D%E5%B9%B3/dp/4297136139/ref=sr_1_3?__mk_ja_JP=%E3%82%AB%E3%82%BF%E3%82%AB%E3%83%8A&crid=26EDW40QRJ39B&keywords=Spring&qid=1707758787&sprefix=spring%2Caps%2C166&sr=8-3-spons&sp_csd=d2lkZ2V0TmFtZT1zcF9hdGY&psc=1 "プロになるためのSpring入門")にのっています、非常におすすめです。
- 追記: Postgresのテーブルと対応したJavaのModel(Entity)をDBにinsertする際、enum型のmappingが上手くいかず、enum型なのに文字列を挿入しようとしているとエラーが出ました。フィールドに@Enumerated(TYPE.STRING)を付与すれば、EntityとTableのenum型のmappingは上手くいくようですが、結局解決しませんでした。わかる方がいれば教えていただきたいです。-->
### 3. **Spring Securityを理解する**
- 一番の難関です、まず仕組みを理解するのに相当時間がかかりました。今まで無事に動作していたアプリにSpring Securityを導入した途端にエラー祭りでした。
- 様々な機能がありとてもパワフルです、数多くのサイトを参考にしました、日本語の情報が少なく海外のサイトにしかない情報もありました。様々経験してわかったことは、**公式のドキュメントが最も信頼できる**ということです。分かりづらい表現が多いですが結局すべてドキュメント通りであるし、個人のサイトは古かったり間違ったりしているものが多いです。Securiy周りは今後も勉強していきたいと思います。
