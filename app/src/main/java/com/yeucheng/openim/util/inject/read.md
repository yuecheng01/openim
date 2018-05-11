注解实现快速绑定view,点击事件(阻止连续点击).长按事件.
用法:

在activity中的onCreate()调用

ViewUtils.inject(this);
 

绑定控件:
	在View声明上使用注解;
	@FindView(R.id.bt1)
	private Button mButton;



点击事件(方法名随意取名,修饰符可随意,private,public均可):

	//单个按钮点击事件:
	@OnClick(R.id.bt1)
	private void click(View view){
		Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();	

	}

	
	//多个按钮点击事件:
	@OnClick({R.id.bt1,R.id.tv2})
	private void click(View view){
		switch(){
			case R.id.bt1:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;

			case R.id.tv2:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;
		}

	}
	//防止连续点击:
	@OnClick(value ={R.id.bt1,R.id.tv2},preventRepetClick = true)
	private void click(View view){
		switch(){
			case R.id.bt1:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;

			case R.id.tv2:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;
		}

	}

长按点击事件:
	
	@OnLongClick({R.id.bt1,R.id.tv2})
	private void click(View view){
		switch(){
			case R.id.bt1:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;

			case R.id.tv2:
			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
			break;
		}

	}

	@CheckNet 加入该注解,表示点击事件触发之前,首先检查网络连接,有网络才执行点击事件,无网络则直接返回不执行点击事件,如下:
	      @CheckNet
	      @OnLongClick({R.id.bt1,R.id.tv2})
            private void click(View view){
          		switch(){
          			case R.id.bt1:
          			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
          			break;

          			case R.id.tv2:
          			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
          			break;
          		}

            }

          @CheckNet
          @OnClick({R.id.bt1,R.id.tv2})
          	private void click(View view){
          		switch(){
          			case R.id.bt1:
          			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
          			break;

          			case R.id.tv2:
          			Toast.makeText(this, "onclick", Toast.LENGTH_SHORT).show();
          			break;
          		}

          	}