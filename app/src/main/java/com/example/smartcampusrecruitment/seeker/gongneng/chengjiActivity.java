package com.example.smartcampusrecruitment.seeker.gongneng;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.ScoreAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.AcademicRecordDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chengjiActivity extends AppCompatActivity {
    private Spinner spinner_xn_cj,spinner_xq_cj;
    private EditText edit_search_cj;
    private Button btn_search_cj;
    private RecyclerView rv_detail_cj;
    private LineChart chengji_chart_data;
    private ScoreAdapter scoreAdapter;
    private List<AcademicRecordDTO> academicRecordDTOList = new ArrayList<>();
    private List<String> xuenianList=new ArrayList<>();
    private List<String> xueqiList=new ArrayList<>();
    private ApiService api = RetrofitClient.getInstance().create(ApiService.class);
    private SeekerDTO student=new SeekerDTO();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chengji);
        initView();
        initData();
        onClick();
        setupSpinnerListener();
    }

    private void setupSpinnerListener() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String xn = (String) spinner_xn_cj.getSelectedItem();
                String xq = (String) spinner_xq_cj.getSelectedItem();
                if (xn != null && xq != null) {
                    showLineChart(xn, xq);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinner_xn_cj.setOnItemSelectedListener(listener);
        spinner_xq_cj.setOnItemSelectedListener(listener);
    }

    private void onClick() {
        btn_search_cj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchContent=edit_search_cj.getText().toString();
                String xn=spinner_xn_cj.getSelectedItem().toString();
                String xq=spinner_xq_cj.getSelectedItem().toString();
                searchByCourse(searchContent,xn,xq);
                scoreAdapter.notifyDataSetChanged();
            }
        });

    }

    private void searchByCourse(String searchContent, String xn, String xq) {
        List<AcademicRecordDTO> displayList=new ArrayList<>();
        String kw = searchContent == null ? "" : searchContent.toLowerCase().trim();
        for (AcademicRecordDTO a:academicRecordDTOList){
            if (a.getAcademicYear().equals(xn)&&a.getSemester().equals(xq)){
                if(a.getSubjectName().toLowerCase().contains(kw))
                    displayList.add(a);
            }
        }
        scoreAdapter.updateData(displayList);
        scoreAdapter.notifyDataSetChanged();
        if (displayList.isEmpty()) {
            Toast.makeText(this, "没有找到符合条件的课程", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        student.setStudentId(getIntent().getStringExtra("studentId"));
        student.setGraduationSchool(getIntent().getStringExtra("school"));
        api.getScoreBySchoolAndStudentId(student.getGraduationSchool(),student.getStudentId()).enqueue(new Callback<List<AcademicRecordDTO>>() {
            @Override
            public void onResponse(Call<List<AcademicRecordDTO>> call, Response<List<AcademicRecordDTO>> response) {
                if (response.isSuccessful()&&response.body()!=null){
                    academicRecordDTOList=response.body();
                    xuenianList=academicRecordDTOList.stream()
                            .map(AcademicRecordDTO::getAcademicYear)
                            .distinct().collect(Collectors.toList());
                    xueqiList=academicRecordDTOList.stream()
                            .map(AcademicRecordDTO::getSemester)
                            .distinct().collect(Collectors.toList());

                    ArrayAdapter<String> adapterXncj=new ArrayAdapter<String>(chengjiActivity.this, android.R.layout.simple_spinner_item,xuenianList);
                    ArrayAdapter<String> adapterXqcj=new ArrayAdapter<String>(chengjiActivity.this, android.R.layout.simple_spinner_item,xueqiList);

                    adapterXncj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapterXqcj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner_xn_cj.setAdapter(adapterXncj);
                    spinner_xq_cj.setAdapter(adapterXqcj);

                    if (!xuenianList.isEmpty() && !xueqiList.isEmpty()) {
                        spinner_xn_cj.setSelection(0);
                        spinner_xq_cj.setSelection(0);
                        showLineChart(xuenianList.get(0), xueqiList.get(0));
                    }
                    scoreAdapter = new ScoreAdapter(academicRecordDTOList);
                    rv_detail_cj.setAdapter(scoreAdapter);
                }else {
                    Log.e("API-ERR", "code=" + response.code() + "  body=" + response.errorBody());
                    Toast.makeText(chengjiActivity.this,"成绩获取失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<AcademicRecordDTO>> call, Throwable t) {
                Toast.makeText(chengjiActivity.this,"成绩获取网络错误",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLineChart(String xn, String xq) {
        List<AcademicRecordDTO> lineList = searchByXnAndXq(xn, xq);

        // 1. 按课程名去重，只保留最新一次成绩（或者第一条）
        Map<String, AcademicRecordDTO> unique = new LinkedHashMap<>();
        for (AcademicRecordDTO d : lineList) unique.put(d.getSubjectName(), d);
        List<AcademicRecordDTO> uniqueList = new ArrayList<>(unique.values());

        // 2. 构造 Entry 和标签
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < uniqueList.size(); i++) {
            entries.add(new Entry(i, uniqueList.get(i).getScore().floatValue()));
            labels.add(uniqueList.get(i).getSubjectName());
        }

        // 3. 设置 x 轴标签
        XAxis xAxis = chengji_chart_data.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);   // 斜向显示，避免重叠
        xAxis.setGranularity(1f);           // 强制一格一个标签
        xAxis.setLabelCount(labels.size(), false); // 标签个数 = 数据点个数
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);


        YAxis yAxis = chengji_chart_data.getAxisLeft();
        yAxis.setAxisMinimum(0); // 设置Y轴最小值

        chengji_chart_data.getDescription().setEnabled(false);  // 隐藏图表描述（右下角默认的"Description"）
        chengji_chart_data.setTouchEnabled(true);               // 允许触摸交互（缩放、滑动等）
        chengji_chart_data.setDragDecelerationEnabled(true);    // 拖动后惯性滑动效果
        chengji_chart_data.setScaleEnabled(true);               // 允许缩放
        chengji_chart_data.setDrawGridBackground(false);        // 不绘制网格背景（默认灰色）
        chengji_chart_data.setDrawBorders(false);               // 不绘制图表边框

        LineDataSet set = new LineDataSet(entries, "成绩");  // 创建数据集，`entries` 是折线图的数据点集合
        set.setColor(Color.GREEN);          // 设置折线颜色
        set.setCircleColor(Color.BLACK);    // 设置数据点的圆圈颜色
        set.setLineWidth(2f);                               // 设置折线宽度（2像素）
        set.setCircleRadius(3f);                            // 设置数据点圆圈半径
        set.setValueTextSize(10f);                          // 设置数据点数值的文字大小
        set.setValueTextColor(Color.GRAY); // 设置数据点数值的文字颜色
        set.setAxisDependency(YAxis.AxisDependency.LEFT);    // 设置数据依赖左侧Y轴

        LineData data = new LineData(set);
        data.setValueTextColor(Color.BLACK);
        chengji_chart_data.setData(data);
        chengji_chart_data.invalidate();
    }

    private List<AcademicRecordDTO> searchByXnAndXq(String xn, String xq) {
        List<AcademicRecordDTO> filterList=new ArrayList<>();
        for (AcademicRecordDTO a:academicRecordDTOList){
            if (xn.equals(a.getAcademicYear())&&xq.equals(a.getSemester()))
                filterList.add(a);
        }
        return filterList;
    }

    private void initView() {
        spinner_xn_cj=findViewById(R.id.spinner_xn_cj);
        spinner_xq_cj=findViewById(R.id.spinner_xq_cj);
        edit_search_cj=findViewById(R.id.edit_search_cj);
        btn_search_cj=findViewById(R.id.btn_search_cj);
        rv_detail_cj=findViewById(R.id.rv_detail_cj);
        chengji_chart_data=findViewById(R.id.chengji_chart_data);

        scoreAdapter=new ScoreAdapter(academicRecordDTOList);
        rv_detail_cj.setLayoutManager(new LinearLayoutManager(this));
        rv_detail_cj.setAdapter(scoreAdapter);
    }
}