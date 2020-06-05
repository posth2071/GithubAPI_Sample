Retrofit2 사용예시 - Github REST API + MVP 패턴 + Glide  사용



완성 모습
패키지 구조

패키지 구조

View : 화면 표시 및 사용자 이벤트 Presenter에게 전달해서 처리를 위임 (Activity)
Presenter : View - Model 사이 매개체 (View로부터 받은 이벤트를 처리하고 Model을 업데이트)
Contract : View - Presenter간의 사용할 이벤트를 정의한 인터페이스 
Model : 데이터, Presenter를 통해 View와 분리한 MVP패턴 구현 
Network : Retrofit REST 메서드 정의 인터페이스 + Client 구현
Adapters : RESTful API로 받아온 사용자 목록을 보여줄 RecyclerView를 관리하는 Adatper


[Github REST API]는 많은 기능들을 RESTful로 제공 (저장소, Issue, User ...)
User 목록과 각 User 정보 (사진 + 이름 + 지역 + URL + Follower 수 + Following 수)를 화면에 보여주도록 만들겁니다.

Github API는 User 목록과 각각 상세정보를 제공하면 좋겠지만, 한번에 제공해주진 않기에 REST요청을 2번으로 나눠 진행합니다

먼저 User 목록 요청 [https://api.github.com/users]
    : 30명의 User 목록 반환
각 User 정보 요청 [https://api.github.com/users/{유저이름}]
    : 프로필 사진 + 이름 + 지역 + URL + Follower / Following 수 
     30명의 User 전부 요청 ( 총 30번 REST API )
이렇게 2번의 REST Request [ 유저 목록 -> 각 User 상세정보 ] 순서로 기능을 구현합니다.



구현하기
1. Contract 인터페이스 정의
UserListContract
View - Presenter - Model 상호간의 연결을 Contract 인터페이스 정의

UserListContract.interface
public interface UserListContract {

    // 1️⃣ Presenter - Model 연결 인터페이스
    interface Model {
    
    	// Presenter 구현 & View 호출
        interface onFinishedListener {
            void onFinished(List<User> users);	// 통신 성공 시 사용

            void onFailure(Throwable t);	// 통신 실패 시 사용
        }
		
        // Model 구현 & Presenter 호출
        void getUserList(onFinishedListener onFinishedListener);	// Presenter가 Model에게 데이터 요청
    }
    
    // 2️⃣ View - Presenter 연결 인터페이스, View 구현 & Presneter 호출
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
}
1️⃣ Model : Presenter-Model 연결 위한 Interface
       - onFinishedListener 부분은 Presenter에서 구현 & setUserList는 Model에서 구현

2️⃣ View : View-Presenter 연결 위한 Interface
       - View 메서드 구현(재정의) & Presenter 메서드 사용

3️⃣ Presenter : View-Presenter 연결 위한 Interface
       - View 메서드 사용 & Presenter 메서드 구현(재정의)
       - View에서 RecyclerView의 Adapter도 분리하기 위해 Presenter에게 위임하도록 정의합니다

이렇게 View - Presenter - Model 간의 의존성 분리를 위한 Contract를 정의합니다



UserAdapterContract
View에서 RecyclerView의 관리를 위한 Adatper도 분리하기 위해서 Presenter에게 위임하도록 Contract를 정의합니다

UserAdapterContract.interface
public interface UserAdapterContract {

    // 1️⃣ Adapter UI 이벤트를 위한 interface
    interface View {
        void notifyAdapter();	// UI Update

        void setOnClickListener(OnItemClick clickListener);	// Click 이벤트 처리위한 리스너
    }
    
    // 2️⃣ Adapter 데이터 관리를 위한 Interface
    interface Model {
        void setData(List<User> users);	// Adapter 데이터 갱신 메서드

        User user(int position);	// 클릭한 user의 정보를 반환하는 메서드
    }
}
1️⃣2️⃣ View & Model 모두 Adapter에서 구현하고 Presenter에서 호출해서 사용하도록 정의합니다
     : 기존 View에서 RecyclerView UI Update를 위한 Adapter 관리를 Presenter에게 위임하고 View에게서 분리합니다  



OnItemClick
위의 UserAdapterContract의 setOnClickListener() 파라미터인 OnItemClick 인터페이스를 정의합니다

RecyclerView에서 Item 클릭 이벤트 처리를 Presenter에게 위임하기 위해 사용합니다

OnItemClick.interface
public interface OnItemClick {
    void onItemClick(int position);	// 단순히 클릭한 User의 번호를 전달합니다
}


2. Model 정의하기
UserList
DTO Model 클래스 UserList를 정의합니다
첫번째 REST API 요청인 30명의 유저목록 데이터를 저장할 용도의 DTO Model 입니다 

UserList.class
public class UserList {

    @SerializedName("login")	// REST 요청결과 중 저장할 속성 - "login"
    private String login;
    
    @SerializedName("id")	// REST 요청결과 중 저장할 속성 - "id"
    private int id;

    public UserList(String login, int id) {	// UserList 생성자(Constructor)
        this.login = login;
        this.id = id;
    }

    // Getter & Setter 메서드 생략
    ...
}


User 
DTO Model 클래스 User를 정의합니다.
서브 REST API 요청인 30명 User 들 각각 세부정보를 저장할 용도의 DTO Model 입니다

User.class
public class User {

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
    
    // Getter & Setter 메서드 생략
    ...
}


UserListModel
DTO가 아닌 실제 Data를 갖는 Model인 UserListModel을 정의합니다
실제 View가 Presenter를 통해 사용하게 되는 Data를 갖는 Model입니다

UserListModel
public class UserListModel implements UserListContract.Model {	
        
        private final String GITHUB_TOKEN = "token {Your Token...}";	// 1️⃣ Github API Token 
        
        List<User> users = new ArrayList<>();
        int count = 0;	// 2️⃣ subCall의 마지막 통신결과 구분위한 count
        
    // UserListContract.Model 인터페이스 메서드 구현
    @Override
    public void getUserList(final onFinishedListener onFinishedListener) {
    
        // Retrofit 인터페이스 구현 
        final ApiInterface service = ApiClient.getInstance().create(ApiInterface.class);
        
        Call<List<UserList>> call = service.getUsers(GITHUB_TOKEN);
        
        // 3️⃣ MainCall 비동기요청(enqueue) 실행 - Callback 리스너 필요  
        call.enqueue(new Callback<List<UserList>>() {
            // onResponse() 구현 - 통신 성공 시 Callback 
            @Override
            public void onResponse(Call<List<UserList>> call, Response<List<UserList>> response) {
                if (!response.isSuccessful()) {	// 4️⃣
                    // Presenter 통신실패 함수 호출 + Log 남기기
                    onFinishedListener.onFailure(
                            RequestFail_Log("MainCall", "onResponse", response)
                    );
                    return;
                }
                // 통신 성공 시 결과 추출 - 30명의 User 저장
                List<UserList> userList = response.body();
                count = userList.size();  // 5️⃣
                
                for (UserList user : userList) {
                    // 6️⃣ 
                    Call<User> subCall = service.getUserInfo(GITHUB_TOKEN, user.getLogin());
                    
                    subCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            count--;	// 통신 성공 시 count 줄이기
                            
                            // 응답Code 체크 - 3xx & 4xx의 실패 코드인지 ?
                            if (!response.isSuccessful()) {	
                                // Presenter 통신실패 함수 호출 + Log 남기기
                                onFinishedListener.onFailure(
                                        RequestFail_Log("SubCall", "onResponse", response)
                                );
                                return;
                            }

                            users.add(response.body());
                            // 현재 count가 0일 경우 -> 30번의 User 정보 요청 중 마지막이 완료된 경우 
                            if ((count) == 0) {
                                // onFinishedListener를 통해 Presenter에게 데이터(users) 전달
                                onFinishedListener.onFinished(users);
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            // Presenter 통신실패 함수 호출 + Log 남기기
                            onFinishedListener.onFailure(
                                    RequestFail_Log("SubCall", "onFailure", t)
                            );
                        }
                    });
                }
            }
            
            // onFailure() 구현(재정의) - 통신 실패 시 Callback
            @Override
            public void onFailure(Call<List<UserList>> call, Throwable t) {
                // Presenter 통신실패 함수 호출 + Log 남기기
                onFinishedListener.onFailure(
                        RequestFail_Log("MainCall", "onFailure", t)
                );
            }
        });
    }
    
    // REST Request 실패 시 Log 표시 함수
    //  : onFailure 또는 onResponse 분기 구분 필요 (onResponse 응답Code가 3xx & 4xx일 경우)
    private String RequestFail_Log(String call, String point, Object result) {
        StringBuilder errorMsg = new StringBuilder();

        if (point.compareTo("onResponse")==0) {
            // onResponse에서 응답코드가 3xx & 4xx 일 경우
            Response response = (Response)result;   // Response 타입 캐스팅
            errorMsg.append(String.format("%s: %s Failure, Code [%d] message [%s]", point, call, response.code(), response.message()));
        }
        else if (point.compareTo("onFailure")==0){
            // onFailure에서 호출한 경우 (시스템적 예외)
            Throwable t = (Throwable)result;    // Throwable 타입 캐스팅
            errorMsg.append(String.format("%s: %s Failure, message [$s]", point, call, t.getMessage()));
        }
        Log.d(TAG, errorMsg.toString());    // Log 찍기
        return errorMsg.toString();         // 분기구분된 ErrorMsg 반환
    }
}
1️⃣ Token
     : Github API의 시간 당 요청횟수가 50회로 제한됨으로 Token을 추가해서 시간당 5000회로 설정합니다
     [Github OPEN API 토큰 발급 방법]


[Android] Github API v3 - Personal Access Token 발급 'API key'

Github API v3 - Personal Access Token 발급 'API key' Github API - Token 발급받기 Github API는 REST API 요청 종류별로 시간 당 Request 수를 제한하는 경우가 있습니다 본인의 경우 User리스트를 조회하는 R..

jaejong.tistory.com
2️⃣ count 
     : 첫 통신으로 User 목록을 얻어와 각 User들의 세부정보를 다시 요청하는 MainCall과 subCall로 구분하는데
     30번의 각 user 정보 요청하는 SubCall이 모두 완료되면 한번에 List에 반영하기 위해 count로 종료를 파악합니다 

3️⃣ User 목록을 반환하는 MainCall입니다.
     한번에 각 user들의 정보(사진+지역+팔로워&팔로윙)를 반환하지 않기때문에 Main과 Sub로 나눴습니다 

4️⃣ 비동기 요청인 enqueue는 Callback 리스너의 등록이 필수적입니다.
     Callback 리스너는 onResponse(통신성공)와 onFailure(통신실패) 두개의 Callback 메서드로 구성
         1. onResponse() - 응답 코드 3xx & 4xx에도 Callback 되기 때문에 응답코드 체크 필수
         2. onFailure() - 예외 발생 & 인터넷 연결 끊김 등 시스템적인 이유의 실패일 경우 Callback 

5️⃣ subCall의 마지막 통신완료를 파악하기 위해 User 수를 count에 할당합니다

6️⃣ 각 User 정보를 요청하는 subCall입니다. User 수 만큼 반복 (여기선 30번)

여기서 중요한 점은 Presenter와 Model을 UserListContract.Model 인터페이스를 통해 서로 연결합니다.

Presenter - Model 연결관계
    Presenter : UserListContract.Model의 getUserList() 메서드로 Model에게 데이터를 요청
    Model :  UserListContract.Model 내부 onFinishedListener의 onFinished() & onFailure()로 Presenter에게 데이터 전달


3. Presenter 정의하기
일반적인 MVC 패턴의 View-Model 연결을 분리한 MVP 패턴 구현을 위해 View와 Model 사이 매개체 역할을 하는 Presenter를 정의합니다



UserListPresenter.class
public class UserListPresenter 
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
    public void onFinished(List<User> users) {
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
}


4. Adapter 정의하기
RecyclerView를 관리할 Adapter를 정의합니다.

중요한 점은 일반적으로는 View에서 Adapter를 관리(Data 변경 & UI Update)합니다.
여기선 Adapter도 View에서 분리하여 Presenter에게 관리를 위임해서 Presenter가 Adapter를 통해 UI Update & Data 변경을 하도록 Presenter와 연결되게 정의합니다

UserAdapter.class
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> 
	implements UserAdapterContract.View, UserAdapterContract.Model {
    
    private Context context;
    private OnItemClick onItemClick;
    private List<User> users;
    
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
                
        // Item 세팅 (이름 + 지역 + 블로그URL + Follower & Following)
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
    public void setData(List<User> users) {
        this.users = users;
    }

    // Adapter.View Contract 추상메서드 정의 (Presenter에서 사용)
    @Override
    public User user(int position) {
        return users.get(position);
    }
}


5. Network 정의하기
Github REST API를 사용하기 위한 Retrofit 싱글톤 클래스와 REST 메서드 정의한 Interface를 선언합니다

ApiInterface
먼저 Retrofit REST 요청 메서드를 정의한 Interface를 선언합니다

ApiInterface.interface
public interface ApiInterface {
    
    // GET 요청, User 목록 요청 (MainCall) 
    @GET("users")
    Call<List<UserList>> getUsers(@Header("Authorization") String token);
    
    
    // GET 요청, 각 User 세부 정보 요청 (SubCall)
    @GET("users/{login}")	// {login} 부분을 함수인자로 전달받도록 설정
    Call<User> getUserInfo(
            @Header("Authorization") String token,
            @Path("login") String login
    );
}


ApiClient
다음으로 Retrofit 인스턴스를 정의하는 클래스를 선언합니다.

해당 Retrofit 인스턴스는 매번 새로운 인스턴스를 생성하는 건 비효율적이기 때문에,
싱글톤(Singleton)으로 정의해서 앱 Lifecycler 단위 동안 단일 인스턴스를 유지하도록 합니다

ApiClient.class
public class ApiClient {
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
}
Retrofit 객체를 싱글톤으로 구현하기 위해서 private(접근한정자) 설정과 getInstance() 메서드로만 접근가능하게 구현

ApiClient 생성자 부분에서 한번만 인스턴스 할당하기 때문에 싱글톤으로 구현이 가능합니다

HTTP 요청 내용과 결과를 로그로 확인하기 위해서 HttpLoggingInterceptor를 사용했습니다.



결과 GIF

완성 모습
[Github 소스 보러가기]


posth2071/GithubAPI_Sample

Contribute to posth2071/GithubAPI_Sample development by creating an account on GitHub.

github.com
