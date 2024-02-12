# My Libraryとは
My Libraryは読書を管理するアプリです。Spring Boot × Thymeleafで作成しました。
まだ完全に完成してはいませんが、日々更新していきます。

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

# 主な機能一覧
1. **ユーザーログイン機能**
- ユーザーログインを実装することで自分だけの読書リストを作成できます
- remember-me機能を実装し、煩わしいログイン処理から解放されます
2. **読書中の本を管理(CRUD)**
- 今何の本を読んでいるかを記録し、メモ等自由に書き込めます
3. **本棚に読み終えた本を保管(CRUD)**
- 読み終えた本は本棚に保管されます、これで自分が今までどんな本を読んだか忘れません
- もちろん後から内容を編集することも可能です
- さらに検索条件を指定し本を詳細に検索することもできます

# セキュリティ対策
1. **CSRFトークンを使用**
- defalutのCSRF対策を有効化し、ログイン処理から適用されるようにカスタマイズしました。<br>
postリクエスト時にcsrfトークンを送信しています。
2. **CORS対策**
- 異なるオリジンでのリソース共有を防ぐため、ローカルホストからのリクエストしか受け付けていないようにしています。
3. **remember-meトークンはデータベースに記録**
- 単純なユーザー名とパスワードのハッシュでは脆弱性があり、ユーザー名に関してはCookieでばれてしまいます。さらにアプリを再起動すると値が変わってしまい、ログインを有効にするためKeyを一定にしなければいけません。これはCSRF攻撃のリスクがあります。
- それに対して、トークンをリポジトリーを管理する方法ではその心配はありません。この場合remember-meトークンは、シリーズ（主キー）とトークンで構成されています。トークンは毎回生成されますがDBに記録されているため、アプリを再起動してもログインは継続します。
- Spring Securityではデフォルトでセッション固定保護が有効になっているため、セッションハイジャッキングへの対策もされています。<br>
※まだまだ対策は不十分です、勉強中であるため間違っている可能性は大いにあります。

