# Retrofit2 사용예시
## Github REST API + MVP 패턴 + Glide&nbsp; 사용
![test](https://user-images.githubusercontent.com/51239479/83843959-0c579280-a741-11ea-9b0f-6841fac26db8.JPG){: width="100" height="100"}

![GIF1](https://user-images.githubusercontent.com/51239479/83847131-aa019080-a746-11ea-85bb-ba3499b3f679.gif)

<p><span style="color: #000000;"><!--Title 시작--></span></p>
<p class="editor_title">Retrofit2 사용예시 - Github REST API + MVP 패턴 + Glide&nbsp; 사용</p>
<p><span style="color: #000000;"><!--Title 종료--></span></p>
<p>[##_Image|kage@pv9OU/btqDDvIEwso/bDck1qvCt8gxgiAWDdKRaK/img.gif|alignCenter|data-filename="Honeycam 2020-04-23 02-13-12.gif" data-origin-width="398" data-origin-height="819" width="268" height="NaN" data-ke-mobilestyle="widthContent"|완성 모습||_##]</p>
<p><!-- 소제목 시작 --></p>
<h2 class="editor_sub_title">패키지 구조</h2>
<p>[##_Image|kage@cu6nts/btqECiVipga/emo0Mizklt9aVZMwZ5lEuK/img.png|alignCenter|data-filename="패키지구조(펼침).png" data-origin-width="395" data-origin-height="533" width="322" height="NaN" data-ke-mobilestyle="widthContent"|패키지 구조||_##]</p>
<p style="position: absolute;">&nbsp;</p>
<p><!-- 소제목 종료 --></p>
<ul style="list-style-type: disc;" data-ke-list-type="disc">
<li><b>View</b> : 화면 표시 및 사용자 이벤트 Presenter에게 전달해서 처리를 위임 (<u>Activity</u>)</li>
<li><b>Presenter</b> : View - Model 사이 <u>매개체</u> (View로부터 받은 이벤트를 처리하고 Model을 업데이트)</li>
<li><b>Contract</b> : View - Presenter간의 사용할 <u>이벤트</u>를 정의한 인터페이스&nbsp;</li>
<li><b>Model</b> : 데이터, Presenter를 통해 View와 분리한 MVP패턴 구현&nbsp;</li>
<li><b>Network</b> : <u>Retrofit</u> REST 메서드 정의 인터페이스 + <u>Client</u> 구현</li>
<li><b>Adapters</b> : RESTful API로 받아온 사용자 목록을 보여줄 RecyclerView를 관리하는 Adatper</li>
</ul>
<p>&nbsp;</p>
<p><a href="https://developer.github.com/v3/" target="_blank" rel="noopener">[Github REST API]</a>는 많은 기능들을 RESTful로 제공 (저장소, Issue, User ...)<br />User <u>목록</u>과 각 User <u>정보</u> (사진 + 이름 + 지역 + URL + Follower 수 + Following 수)를 화면에 보여주도록 만들겁니다.</p>
<p><u>Github API</u>는 User 목록과 각각 상세정보를 제공하면 좋겠지만, 한번에 제공해주진 않기에 <u>REST요청</u>을 <u>2번</u>으로 나눠 진행합니다</p>
<blockquote data-ke-style="style2">
<ol style="list-style-type: decimal;" data-ke-list-type="decimal">
<li>먼저 User 목록 요청 <a href="https://api.github.com/users" target="_blank" rel="noopener">[https://api.github.com/users]</a><br />&nbsp; &nbsp; : 30명의 User 목록 반환</li>
<li>각 User 정보 요청 <a href="https://api.github.com/users/mojombo" target="_blank" rel="noopener">[https://api.github.com/users/{유저이름}]</a><br />&nbsp; &nbsp; : 프로필 사진 + 이름 + 지역 + URL + Follower / Following 수&nbsp;<br />&nbsp; &nbsp; &nbsp;30명의 User 전부 요청 ( 총 30번 REST API )</li>
</ol>
<p>이렇게 2번의 <u>REST Request</u> [ 유저 목록 -&gt; 각 User 상세정보 ] 순서로 기능을 구현합니다.</p>
</blockquote>
<p>&nbsp;</p>
<p><!-- 소제목 시작 --></p>
<h2 class="editor_sub_title">구현하기</h2>
<h3 data-ke-size="size23">1. Contract 인터페이스 정의</h3>
<h4 data-ke-size="size20">UserListContract</h4>
<p>View - Presenter - Model 상호간의 연결을 <u>Contract</u> 인터페이스 정의</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserListContract.interface</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public interface UserListContract {

    // 1️⃣ Presenter - Model 연결 인터페이스
    interface Model {
    
    	// Presenter 구현 &amp; View 호출
        interface onFinishedListener {
            void onFinished(List&lt;User&gt; users);	// 통신 성공 시 사용

            void onFailure(Throwable t);	// 통신 실패 시 사용
        }
		
        // Model 구현 &amp; Presenter 호출
        void getUserList(onFinishedListener onFinishedListener);	// Presenter가 Model에게 데이터 요청
    }
    
    // 2️⃣ View - Presenter 연결 인터페이스, View 구현 &amp; Presneter 호출
    interface View {
        void showProgress();	// 프로그래스바 보이기

        void hideProgress();	// 프로그래스바 숨기기

        void showToast(String message);	// Toast 메시지 띄우기

        void onResponseFailure(Throwable throwable);	// 통신 실패 시 Presenter가 View 메서드 호출

    }
    
    // 3️⃣ View - Presenter 연결 interface, Presenter 구현 / View 호출
    interface Presenter {
        void onDestroy();	// View 소멸 시 Presenter 해제 위한 메서드

        void requestDataFromServer();	// User 데이터 재요청
        
        // RecyclerView Adapter도 View에서 분리하기 위해 Presenter에게 위임
        void setUserAdpaterModel(UserAdapterContract.Model model);

        void setUserAdpaterView(UserAdapterContract.View view);	
    }
}</code></pre>
<p>1️⃣ <u>Model</u> : Presenter-Model 연결 위한 Interface<br />&nbsp; &nbsp; &nbsp; &nbsp;- onFinishedListener 부분은 Presenter에서 구현 &amp; setUserList는 Model에서 구현</p>
<p>2️⃣ <u>View</u> : View-Presenter 연결 위한 Interface<br />&nbsp; &nbsp; &nbsp; &nbsp;- View 메서드 구현(재정의) &amp; Presenter 메서드 사용</p>
<p>3️⃣ <u>Presenter</u> : View-Presenter 연결 위한 Interface<br />&nbsp; &nbsp; &nbsp; &nbsp;- View 메서드 사용 &amp; Presenter 메서드 구현(재정의)<br />&nbsp; &nbsp; &nbsp; &nbsp;- View에서 RecyclerView의 Adapter도 분리하기 위해 Presenter에게 위임하도록 정의합니다</p>
<p><!-- 코드블럭 종료 --></p>
<p>이렇게 View - Presenter - Model 간의 의존성 분리를 위한 <u>Contract</u>를 정의합니다</p>
<p>&nbsp;</p>
<h4 data-ke-size="size20">UserAdapterContract</h4>
<p><u>View</u>에서 RecyclerView의 관리를 위한 <u>Adatper</u>도 분리하기 위해서 <u>Presenter</u>에게 위임하도록 Contract를 정의합니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserAdapterContract.interface</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public interface UserAdapterContract {

    // 1️⃣ Adapter UI 이벤트를 위한 interface
    interface View {
        void notifyAdapter();	// UI Update

        void setOnClickListener(OnItemClick clickListener);	// Click 이벤트 처리위한 리스너
    }
    
    // 2️⃣ Adapter 데이터 관리를 위한 Interface
    interface Model {
        void setData(List&lt;User&gt; users);	// Adapter 데이터 갱신 메서드

        User user(int position);	// 클릭한 user의 정보를 반환하는 메서드
    }
}</code></pre>
<p>1️⃣2️⃣ View &amp; Model 모두 Adapter에서 구현하고 Presenter에서 호출해서 사용하도록 정의합니다<br />&nbsp; &nbsp; &nbsp;: 기존 View에서 RecyclerView <u>UI Update</u>를 위한 Adapter 관리를 <u>Presenter</u>에게 위임하고 View에게서 분리합니다&nbsp;&nbsp;</p>
<p><!-- 코드블럭 종료 --></p>
<p>&nbsp;</p>
<h4 data-ke-size="size20">OnItemClick</h4>
<p>위의 UserAdapterContract의 <u>setOnClickListener()</u> 파라미터인 <u>OnItemClick</u> 인터페이스를 정의합니다</p>
<p>RecyclerView에서 Item 클릭 이벤트 처리를 Presenter에게 위임하기 위해 사용합니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">OnItemClick.interface</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public interface OnItemClick {
    void onItemClick(int position);	// 단순히 클릭한 User의 번호를 전달합니다
}</code></pre>
<p>&nbsp;</p>
<p><!-- 코드블럭 종료 --></p>
<h3 data-ke-size="size23">2. Model 정의하기</h3>
<h4 data-ke-size="size20">UserList</h4>
<p><u>DTO</u> Model 클래스 UserList를 정의합니다<br />첫번째 REST API 요청인 30명의 <u>유저목록</u> 데이터를 저장할 용도의 DTO Model 입니다&nbsp;</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserList.class</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class UserList {

    @SerializedName("login")	// REST 요청결과 중 저장할 속성 - "login"
    private String login;
    
    @SerializedName("id")	// REST 요청결과 중 저장할 속성 - "id"
    private int id;

    public UserList(String login, int id) {	// UserList 생성자(Constructor)
        this.login = login;
        this.id = id;
    }

    // Getter &amp; Setter 메서드 생략
    ...
}</code></pre>
<p>&nbsp;</p>
<p><!-- 코드블럭 종료 --></p>
<h4 data-ke-size="size20">User&nbsp;</h4>
<p><u>DTO</u> Model 클래스 User를 정의합니다.<br />서브 REST API 요청인 30명 User 들 <u>각각 세부정보</u>를 저장할 용도의 DTO Model 입니다</p>
<p><!-- 소제목 종료 --></p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">User.class</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class User {

    @SerializedName("login")		// 'login' - 이름 
    private String login;
    
    @SerializedName("id")		// 'id' - 순서
    private int id;
    
    @SerializedName("avatar_url")	// 'avatar_url' - 프로필사진 URL
    private String image;
    
    @SerializedName("blog")		// 'blog' - 블로그 URL
    private String blog;
    
    @SerializedName("location")		// 'location' - 지역
    private String location;
    
    @SerializedName("followers")	// 'followers' - 팔로워 수
    private int followers;
    
    @SerializedName("following")	// 'following' - 팔로윙 수
    private int following;
    
    // User 생성자(Constructor)
    public User(String login, int id, String image, String blog, String location, int followers, int following) {
        ...
    }
    
    // Getter &amp; Setter 메서드 생략
    ...
}</code></pre>
<p>&nbsp;</p>
<h4 data-ke-size="size20">UserListModel</h4>
<p>DTO가 아닌 <u>실제 Data</u>를 갖는 Model인 UserListModel을 정의합니다<br />실제 View가 Presenter를 통해 사용하게 되는 Data를 갖는 Model입니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserListModel</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class UserListModel implements UserListContract.Model {	
        
        private final String GITHUB_TOKEN = "token {Your Token...}";	// 1️⃣ Github API Token 
        
        List&lt;User&gt; users = new ArrayList&lt;&gt;();
        int count = 0;	// 2️⃣ subCall의 마지막 통신결과 구분위한 count
        
    // UserListContract.Model 인터페이스 메서드 구현
    @Override
    public void getUserList(final onFinishedListener onFinishedListener) {
    
        // Retrofit 인터페이스 구현 
        final ApiInterface service = ApiClient.getInstance().create(ApiInterface.class);
        
        Call&lt;List&lt;UserList&gt;&gt; call = service.getUsers(GITHUB_TOKEN);
        
        // 3️⃣ MainCall 비동기요청(enqueue) 실행 - Callback 리스너 필요  
        call.enqueue(new Callback&lt;List&lt;UserList&gt;&gt;() {
            // onResponse() 구현 - 통신 성공 시 Callback 
            @Override
            public void onResponse(Call&lt;List&lt;UserList&gt;&gt; call, Response&lt;List&lt;UserList&gt;&gt; response) {
                if (!response.isSuccessful()) {	// 4️⃣
                    return;
                }
                // 통신 성공 시 결과 추출 - 30명의 User 저장
                List&lt;UserList&gt; userList = response.body();
                count = userList.size();  // 5️⃣
                
                for (UserList user : userList) {
                    // 6️⃣ 
                    Call&lt;User&gt; subCall = service.getUserInfo(GITHUB_TOKEN, user.getLogin());
                    
                    subCall.enqueue(new Callback&lt;User&gt;() {
                        @Override
                        public void onResponse(Call&lt;User&gt; call, Response&lt;User&gt; response) {
                            count--;	// 통신 성공 시 count 줄이기
                            if (!response.isSuccessful()) {	// 응답Code 체크 - 3xx &amp; 4xx의 실패 코드인지 ?
                                return;
                            }

                            users.add(response.body());
                            // 현재 count가 0일 경우 -&gt; 30번의 User 정보 요청 중 마지막이 완료된 경우 
                            if ((count) == 0) {
                                // onFinishedListener를 통해 Presenter에게 데이터(users) 전달
                                onFinishedListener.onFinished(users);
                            }
                        }
                        
                        @Override
                        public void onFailure(Call&lt;User&gt; call, Throwable t) {
                            Log.d(TAG, t.toString());
                            // subCall 통신 요청 실패 시 onFinishedListener를 통해 Presenter에 실패 전달 
                            onFinishedListener.onFailure(t);
                        }
                    });
                }
            }
            
            // onFailure() 구현(재정의) - 통신 실패 시 Callback
            @Override
            public void onFailure(Call&lt;List&lt;UserList&gt;&gt; call, Throwable t) {
                Log.d(TAG, t.toString());
                // 통신 실패 시 Presenter에게 onFailurer()로 실패 전달
                onFinishedListener.onFailure(t);
            }
        });

    }
}</code></pre>
<p>1️⃣ <u>Token</u><br />&nbsp; &nbsp; &nbsp;: Github API의 시간 당 요청횟수가 50회로 제한됨으로 Token을 추가해서 시간당 5000회로 설정합니다<br />&nbsp; &nbsp; &nbsp;<a href="https://jaejong.tistory.com/94" target="_blank" rel="noopener">[Github OPEN API 토큰 발급 방법]</a></p>
<figure id="og_1591288101508" contenteditable="false" data-ke-type="opengraph" data-og-type="article" data-og-title="[Android] Github API v3 - Personal Access Token 발급 'API key'" data-og-description="Github API v3 - Personal Access Token 발급 'API key' Github API - Token 발급받기 Github API는 REST API 요청 종류별로 시간 당 Request 수를 제한하는 경우가 있습니다 본인의 경우 User리스트를 조회하는 R.." data-og-host="jaejong.tistory.com" data-og-source-url="https://jaejong.tistory.com/94" data-og-url="https://jaejong.tistory.com/94" data-og-image="https://scrap.kakaocdn.net/dn/rqck4/hyGiTrGzZX/r3hSTMFqnEi8lFjfw1uiV1/img.png?width=800&amp;height=347&amp;face=0_0_800_347,https://scrap.kakaocdn.net/dn/dc5qad/hyGiJJoCM1/MmwUNP2pfNDIj5DM1ZFRtk/img.png?width=800&amp;height=347&amp;face=0_0_800_347,https://scrap.kakaocdn.net/dn/OjekC/hyGiTZxuA4/nVrhJpIXcKD0qkpx8W9TB1/img.png?width=1440&amp;height=626&amp;face=0_0_1440_626"><a href="https://jaejong.tistory.com/94" target="_blank" rel="noopener" data-source-url="https://jaejong.tistory.com/94">
<div class="og-image" style="background-image: url('https://scrap.kakaocdn.net/dn/rqck4/hyGiTrGzZX/r3hSTMFqnEi8lFjfw1uiV1/img.png?width=800&amp;height=347&amp;face=0_0_800_347,https://scrap.kakaocdn.net/dn/dc5qad/hyGiJJoCM1/MmwUNP2pfNDIj5DM1ZFRtk/img.png?width=800&amp;height=347&amp;face=0_0_800_347,https://scrap.kakaocdn.net/dn/OjekC/hyGiTZxuA4/nVrhJpIXcKD0qkpx8W9TB1/img.png?width=1440&amp;height=626&amp;face=0_0_1440_626');">&nbsp;</div>
<div class="og-text">
<p class="og-title">[Android] Github API v3 - Personal Access Token 발급 'API key'</p>
<p class="og-desc">Github API v3 - Personal Access Token 발급 'API key' Github API - Token 발급받기 Github API는 REST API 요청 종류별로 시간 당 Request 수를 제한하는 경우가 있습니다 본인의 경우 User리스트를 조회하는 R..</p>
<p class="og-host">jaejong.tistory.com</p>
</div>
</a></figure>
<p>2️⃣ <u>count</u> <br />&nbsp; &nbsp; &nbsp;: 첫 통신으로 User 목록을 얻어와 각 User들의 세부정보를 다시 요청하는 <u>MainCall</u>과 <u>subCall</u>로 구분하는데<br />&nbsp; &nbsp; &nbsp;30번의 각 user 정보 요청하는 SubCall이 모두 완료되면 한번에 List에 반영하기 위해 count로 종료를 파악합니다&nbsp;</p>
<p>3️⃣ User 목록을 반환하는 <u>MainCall</u>입니다.<br />&nbsp; &nbsp; &nbsp;한번에 각 user들의 정보(사진+지역+팔로워&amp;팔로윙)를 반환하지 않기때문에 Main과 Sub로 나눴습니다&nbsp;</p>
<p>4️⃣ 비동기 요청인 enqueue는 Callback 리스너의 등록이 필수적입니다.<br />&nbsp; &nbsp; &nbsp;Callback 리스너는 onResponse(통신성공)와 onFailure(통신실패) 두개의 Callback 메서드로 구성<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;1. onResponse() - 응답 코드 3xx &amp; 4xx에도 Callback 되기 때문에 응답코드 체크 필수<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;2. onFailure() - 예외 발생 &amp; 인터넷 연결 끊김 등 시스템적인 이유의 실패일 경우 Callback&nbsp;</p>
<p>5️⃣ <u>subCall</u>의 마지막 통신완료를 파악하기 위해 User 수를 count에 할당합니다</p>
<p>6️⃣ 각 User 정보를 요청하는 <u>subCall</u>입니다. User 수 만큼 반복 (여기선 30번)</p>
<p><!-- 코드블럭 종료 --></p>
<p><!-- 코드블럭 종료 --></p>
<p>여기서 중요한 점은 Presenter와 Model을 <u>UserListContract.Model</u> 인터페이스를 통해 서로 연결합니다.</p>
<blockquote data-ke-style="style2"><b>Presenter - Model 연결관계</b><br />&nbsp; &nbsp; Presenter : UserListContract.Model의 <u>getUserList()</u> 메서드로 Model에게 데이터를 요청<br />&nbsp; &nbsp; Model :&nbsp; UserListContract.Model 내부 onFinishedListener의 <u>onFinished()</u> &amp; <u>onFailure()</u>로 Presenter에게 데이터 전달</blockquote>
<p>&nbsp;</p>
<h3 data-ke-size="size23">3. Presenter 정의하기</h3>
<p>일반적인 <u>MVC 패턴</u>의 View-Model 연결을 분리한 <u>MVP 패턴</u> 구현을 위해 View와 Model 사이 매개체 역할을 하는 Presenter를 정의합니다</p>
<p>&nbsp;</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserListPresenter.class</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class UserListPresenter 
	implements UserListContract.Presenter, UserListContract.Model.onFinishedListener, OnItemClick {

    private UserListContract.View view;		// View Contract 구현체
    private UserListContract.Model model;	// Model Contract 구현체

    private UserAdapterContract.Model adapterModel;	// Adapter_Model Contract 구현체
    private UserAdapterContract.View adapterView;	// Adapter_View Contract 구현체
    
    // Presenter 생성자, View Contract 구현체 함수인수로 전달
    public UserListPresenter(UserListContract.View view) {
        this.view = view;
        this.model = new UserListModel();	// Model Contract 구현체 선언, UserListModel이 해당 Contract 인터페이스 상속 필요
    }
    
    // View-Presenter 추상메서드 onDestory() 정의 (View에서 사용)
    @Override
    public void onDestroy() {	
        view = null;
    }
    
    // View-Presenter 추상메서드 requestDataFromServer() 정의 (View에서 사용)
    @Override
    public void requestDataFromServer() {	
        if (view != null) {	// View가 소멸된 상태인지?
            view.showProgress();
        }
        model.getUserList(this);
    }
    
    // Presenter-Model 추상메서드 onFinished() 정의 (Model에서 사용)
    @Override
    public void onFinished(List&lt;User&gt; users) {
        // View 존재(소멸) 체크
        if (view != null) {
            view.hideProgress();		// 진행바(프로그래스바) 표시
            adapterModel.setData(users);	// Adatper에 Data 추가
            adapterView.notifyAdapter();	// RecyclerView 갱신
        }
    }

    // Presenter-Model 추상메서드 onFailure() 정의 (Model에서 사용)
    @Override
    public void onFailure(Throwable t) {
        // View 존재(소멸) 체크
        if (view != null) {
            view.onResponseFailure(t);	// View 통신실패 이벤트 호출
            view.hideProgress();	// 진행바(프로그래스바) 숨기기
        }
    }
    
    // View-Presenter 추상메서드 정의 (View에서 사용)
    @Override
    public void setUserAdpaterModel(UserAdapterContract.Model model) {
        adapterModel = model;	// Adapter.Model 할당
    }
    
    // View-Presenter 추상메서드 정의 (View에서 사용)
    @Override
    public void setUserAdpaterView(UserAdapterContract.View view) {
        this.adapterView = view;	// Adatper.View 할당
        this.adapterView.setOnClickListener(this); // Adapter.View에 클릭이벤트 리스너 할당
    }
    
    // OnItemClick 인터페이스 추상메서드 정의 (Adapter에서 사용)
    @Override
    public void onItemClick(int position) {
        view.showToast(position+"번째 User 클릭"); // Adapter에서 Item 클릭이벤트로 해당 Item index 전달
    }
}</code></pre>
<p>&nbsp;</p>
<p><!-- 코드블럭 종료 --></p>
<h3 data-ke-size="size23">4. Adapter 정의하기</h3>
<p><u>RecyclerView</u>를 관리할 Adapter를 정의합니다.</p>
<p>중요한 점은 일반적으로는 View에서 Adapter를 관리(<u>Data 변경</u> &amp; <u>UI Update</u>)합니다.<br />여기선 Adapter도 View에서 분리하여 Presenter에게 관리를 위임해서 Presenter가 Adapter를 통해 UI Update &amp; Data 변경을 하도록 Presenter와 연결되게 정의합니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">UserAdapter.class</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class UserAdapter extends RecyclerView.Adapter&lt;UserAdapter.ViewHolder&gt; 
	implements UserAdapterContract.View, UserAdapterContract.Model {
    
    private Context context;
    private OnItemClick onItemClick;
    private List&lt;User&gt; users;
    
    // UserAdapter 생성자
    public UserAdapter(Context context) {
        this.context = context; // LayoutInflate를 위해 Context를 함수인자로 전달받도록 정의
    }
    
    // onCreateViewHolder 구현
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        
        return new UserAdapter.ViewHolder(view);
    }
    
    // onBindViewHolder 구현 - RecyclerView Item 세팅 부분
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        
        // Glide 이미지 라이브러리 사용
        Glide.with(context)
                .load(user.getImage())
                .apply(new RequestOptions().circleCrop())
                .into(holder.image);
                
        // Item 세팅 (이름 + 지역 + 블로그URL + Follower &amp; Following)
        holder.name.setText(user.getLogin());
        holder.location.setText(user.getLocation());
        holder.blog.setText(user.getBlog());
        holder.follower.setText(String.valueOf(user.getFollowers()));
        holder.following.setText(String.valueOf(user.getFollowing()));
    }
    
    // ViewHolder 클래스 정의
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, location, blog, follower, following;
        ImageView image;
        
        // ViewHolder 생성자
        public ViewHolder(@NonNull View view) {
            super(view);
            
            name = view.findViewById(R.id.user_name);
            location = view.findViewById(R.id.user_location);
            blog = view.findViewById(R.id.user_blog);
            follower = view.findViewById(R.id.user_follower);
            following = view.findViewById(R.id.user_following);

            image = view.findViewById(R.id.user_image);
            
            // RecyclerView Item 클릭리스너 등록 
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // OnItemClick 인터페이스 메서드 사용 (Presenter에게 이벤트 처리 위임)
                    onItemClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    
    // Adapter.View Contract 추상메서드 정의 (Presenter에서 사용)
    @Override
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    // Adapter.View Contract 추상메서드 정의 (Presenter에서 사용)
    @Override
    public void setOnClickListener(OnItemClick clickListener) {
        this.onItemClick = clickListener;
    }

    // Adapter.Model Contract 추상메서드 정의 (Presenter에서 사용)
    @Override
    public void setData(List&lt;User&gt; users) {
        this.users = users;
    }

    // Adapter.View Contract 추상메서드 정의 (Presenter에서 사용)
    @Override
    public User user(int position) {
        return users.get(position);
    }
}</code></pre>
<p><!-- 코드블럭 종료 --></p>
<p>&nbsp;</p>
<h3 data-ke-size="size23">5. Network 정의하기</h3>
<p><u>Github REST API</u>를 사용하기 위한 Retrofit 싱글톤 클래스와 REST 메서드 정의한 Interface를 선언합니다</p>
<h4 data-ke-size="size20">ApiInterface</h4>
<p>먼저 Retrofit REST 요청 메서드를 정의한 <u>Interface</u>를 선언합니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename">ApiInterface.interface</div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public interface ApiInterface {
    
    // GET 요청, User 목록 요청 (MainCall) 
    @GET("users")
    Call&lt;List&lt;UserList&gt;&gt; getUsers(@Header("Authorization") String token);
    
    
    // GET 요청, 각 User 세부 정보 요청 (SubCall)
    @GET("users/{login}")	// {login} 부분을 함수인자로 전달받도록 설정
    Call&lt;User&gt; getUserInfo(
            @Header("Authorization") String token,
            @Path("login") String login
    );
}</code></pre>
<p>&nbsp;</p>
<p><!-- 코드블럭 종료 --></p>
<h4 data-ke-size="size20">ApiClient</h4>
<p>다음으로 <u>Retrofit 인스턴스</u>를 정의하는 클래스를 선언합니다.</p>
<p>해당 Retrofit 인스턴스는 매번 새로운 인스턴스를 생성하는 건 비효율적이기 때문에,<br /><u>싱글톤(Singleton)</u>으로 정의해서 앱 Lifecycler 단위 동안 단일 인스턴스를 유지하도록 합니다</p>
<p><!-- 코드블럭 시작 --></p>
<div class="editor_filename"><span class="editor_filename_body">ApiClient.class</span></div>
<pre id="code_1589214884063" class="java" data-ke-language="java" data-ke-type="codeblock"><code>public class ApiClient {
    private static final String BASE_URL = "https://api.github.com/";	// 기본 Base URL

    public static ApiClient ourInstance = null;	 
    private static Retrofit retrofit = null;	// private 접근한정자로 외부에서 직접 접근 방지
    
    // ApiClient 생성자
    public ApiClient() {
        // ApiClient 타입의 ourInstance 존재 확인
        if (ourInstance == null) {
            // Null이라면 Retrofit 객체 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())	// REST 요청 로그확인 위해 HttpLoggingInterceptor 등록  
                    .build();
        }
    }
    
    // Retrofit 객체 반환 메서드, 전역함수 설정 (public static)
    public static Retrofit getInstance() {
        // ourInstance 존재 확인, 없다면 ApiClient 생성자 호출
        if (ourInstance == null) {
            ourInstance = new ApiClient();
        }
        
        // Retrofit 객체 반환, private 접근한정자로 설정되어서 getInstance() 메서드로만 접근가능
        return retrofit;
    }
    
    // REST API 요청 로그확인을 위해 LoggingInterceptor 생성
    public HttpLoggingInterceptor getIntercepter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }

    public OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(getIntercepter())	// LoggingInterceptor 등록
                .build();

        return client;
    }
}</code></pre>
<p>Retrofit 객체를 싱글톤으로 구현하기 위해서 <u>private(접근한정자)</u> 설정과 <u>getInstance()</u> 메서드로만 접근가능하게 구현</p>
<p>ApiClient 생성자 부분에서 한번만 인스턴스 할당하기 때문에 <u>싱글톤</u>으로 구현이 가능합니다</p>
<p>HTTP 요청 내용과 결과를 로그로 확인하기 위해서 <u>HttpLoggingInterceptor</u>를 사용했습니다.</p>
<p><!-- 코드블럭 종료 --></p>
<p>&nbsp;</p>
<h2 data-ke-size="size26">결과 GIF</h2>
<p>[##_Image|kage@pv9OU/btqDDvIEwso/bDck1qvCt8gxgiAWDdKRaK/img.gif|alignCenter|data-filename="Honeycam 2020-04-23 02-13-12.gif" data-origin-width="398" data-origin-height="819" width="268" height="NaN" data-ke-mobilestyle="widthContent"|완성 모습||_##]</p>
<p><a href="https://github.com/posth2071/GithubAPI_Sample" target="_blank" rel="noopener">[Github 소스 보러가기]</a></p>
<figure id="og_1591287963798" contenteditable="false" data-ke-type="opengraph" data-og-type="object" data-og-title="posth2071/GithubAPI_Sample" data-og-description="Contribute to posth2071/GithubAPI_Sample development by creating an account on GitHub." data-og-host="github.com" data-og-source-url="https://github.com/posth2071/GithubAPI_Sample" data-og-url="https://github.com/posth2071/GithubAPI_Sample" data-og-image="https://scrap.kakaocdn.net/dn/J6PCo/hyGiS0Bjbg/D9onaHO41OF0bddZajinE1/img.jpg?width=400&amp;height=400&amp;face=0_0_400_400"><a href="https://github.com/posth2071/GithubAPI_Sample" target="_blank" rel="noopener" data-source-url="https://github.com/posth2071/GithubAPI_Sample">
<div class="og-image" style="background-image: url('https://scrap.kakaocdn.net/dn/J6PCo/hyGiS0Bjbg/D9onaHO41OF0bddZajinE1/img.jpg?width=400&amp;height=400&amp;face=0_0_400_400');">&nbsp;</div>
<div class="og-text">
<p class="og-title">posth2071/GithubAPI_Sample</p>
<p class="og-desc">Contribute to posth2071/GithubAPI_Sample development by creating an account on GitHub.</p>
<p class="og-host">github.com</p>
</div>
</a></figure>
<p>&nbsp;</p>
<p><span style="color: #000000;"><!--소제목 종료--></span></p>
