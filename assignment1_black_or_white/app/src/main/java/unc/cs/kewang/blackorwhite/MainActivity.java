package unc.cs.kewang.blackorwhite;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView mMoveHistoryTextView;
    private TextView mMoveCountTextView;
    private ImageButton[] mBoardSquares;
    private Button[] mSwitchButton;
    private Button mManualSetDoneButton;
    private Toast mToast;
    private LinearLayout mInfoLinearLayout;
    private GridLayout mBoardGridLayout;
    private GridLayout mControlGridlLayout;
    private GridLayout mSwitchGridLayout;
    private boolean mManualEditMode = false;

    private enum SquareStatus {
        BLACK(0), WHITE(1);
        private final int status;

        SquareStatus(int status) {
            this.status = status;
        }

        public int getValue() {
            return status;
        }
    }

    private SquareStatus[] mBoardStatus;
    private String mOptimalSolution;
    private static final int ALL_WHITE_BOARD = 0;
    private static final int ALL_BLACK_BOARD = 16;
    private static final int ANIMATION_TRANSITION_TIME = 100;
    private int mMoveCount = 0;

    private static final int[][] SWITCH_SQUARES = {
            {0, 1, 2},
            {3, 7, 9, 11},
            {4, 10, 14, 15},
            {0, 4, 5, 6, 7},
            {6, 7, 8, 10, 12},
            {0, 2, 14, 15},
            {3, 14, 15},
            {4, 5, 7, 14, 15},
            {1, 2, 3, 4, 5},
            {3, 4, 5, 9, 13}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up
        mMoveHistoryTextView = (TextView) findViewById(R.id.tv_sequence);
        mMoveCountTextView = (TextView) findViewById(R.id.tv_count);

        mBoardSquares = new ImageButton[16];
        mBoardSquares[0] = (ImageButton) findViewById(R.id.board_00);
        mBoardSquares[1] = (ImageButton) findViewById(R.id.board_01);
        mBoardSquares[2] = (ImageButton) findViewById(R.id.board_02);
        mBoardSquares[3] = (ImageButton) findViewById(R.id.board_03);
        mBoardSquares[4] = (ImageButton) findViewById(R.id.board_04);
        mBoardSquares[5] = (ImageButton) findViewById(R.id.board_05);
        mBoardSquares[6] = (ImageButton) findViewById(R.id.board_06);
        mBoardSquares[7] = (ImageButton) findViewById(R.id.board_07);
        mBoardSquares[8] = (ImageButton) findViewById(R.id.board_08);
        mBoardSquares[9] = (ImageButton) findViewById(R.id.board_09);
        mBoardSquares[10] = (ImageButton) findViewById(R.id.board_10);
        mBoardSquares[11] = (ImageButton) findViewById(R.id.board_11);
        mBoardSquares[12] = (ImageButton) findViewById(R.id.board_12);
        mBoardSquares[13] = (ImageButton) findViewById(R.id.board_13);
        mBoardSquares[14] = (ImageButton) findViewById(R.id.board_14);
        mBoardSquares[15] = (ImageButton) findViewById(R.id.board_15);

        mSwitchButton = new Button[10];
        mSwitchButton[0] = (Button) findViewById(R.id.switch_a);
        mSwitchButton[1] = (Button) findViewById(R.id.switch_b);
        mSwitchButton[2] = (Button) findViewById(R.id.switch_c);
        mSwitchButton[3] = (Button) findViewById(R.id.switch_d);
        mSwitchButton[4] = (Button) findViewById(R.id.switch_e);
        mSwitchButton[5] = (Button) findViewById(R.id.switch_f);
        mSwitchButton[6] = (Button) findViewById(R.id.switch_g);
        mSwitchButton[7] = (Button) findViewById(R.id.switch_h);
        mSwitchButton[8] = (Button) findViewById(R.id.switch_i);
        mSwitchButton[9] = (Button) findViewById(R.id.switch_j);

        mBoardStatus = new SquareStatus[16];
        for (int index = 0; index < 16; ++index) {
            mBoardStatus[index] = SquareStatus.WHITE;
        }
        mMoveCount = 0;
        randomInitializeBoard();
        visualizeBoard();

        mMoveCountTextView.setText(String.valueOf(mMoveCount));
        mMoveHistoryTextView.setMovementMethod(new ScrollingMovementMethod());

        mBoardGridLayout = (GridLayout) findViewById(R.id.board_grid_layout);
        mSwitchGridLayout = (GridLayout) findViewById(R.id.switch_grid_layout);
        mControlGridlLayout = (GridLayout) findViewById(R.id.control_grid_layout);
        mInfoLinearLayout = (LinearLayout) findViewById(R.id.info_linear_layout);
        mManualSetDoneButton = (Button) findViewById(R.id.manual_set_done_button);

        hideManualEditInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manual_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set) {
            mManualEditMode = true;
            showManualEditInterface();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onClickManualSetDone(View view) {
        mManualEditMode = false;
        hideManualEditInterface();
        mMoveCount = 0;
        mMoveCountTextView.setText("0");
        mMoveHistoryTextView.setText("");
    }

    public void onClickSquare(View view) {
        if (!mManualEditMode) {
            return;
        }
        int squareId = view.getId();
        switch (squareId) {
            case R.id.board_00:
                flipSquare(0);
                break;
            case R.id.board_01:
                flipSquare(1);
                break;
            case R.id.board_02:
                flipSquare(2);
                break;
            case R.id.board_03:
                flipSquare(3);
                break;
            case R.id.board_04:
                flipSquare(4);
                break;
            case R.id.board_05:
                flipSquare(5);
                break;
            case R.id.board_06:
                flipSquare(6);
                break;
            case R.id.board_07:
                flipSquare(7);
                break;
            case R.id.board_08:
                flipSquare(8);
                break;
            case R.id.board_09:
                flipSquare(9);
                break;
            case R.id.board_10:
                flipSquare(10);
                break;
            case R.id.board_11:
                flipSquare(11);
                break;
            case R.id.board_12:
                flipSquare(12);
                break;
            case R.id.board_13:
                flipSquare(13);
                break;
            case R.id.board_14:
                flipSquare(14);
                break;
            case R.id.board_15:
                flipSquare(15);
                break;
            default:
                break;
        }
        visualizeBoard();
    }

    public void onClickSwitch(View view) {
        if (mToast != null) {
            mToast.cancel();
        }
        int switchId = view.getId();
        String switchLabel = "";
        switch (switchId) {
            case R.id.switch_a:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[0]);
                switchLabel = "A";
                break;
            case R.id.switch_b:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[1]);
                switchLabel = "B";
                break;
            case R.id.switch_c:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[2]);
                switchLabel = "C";
                break;
            case R.id.switch_d:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[3]);
                switchLabel = "D";
                break;
            case R.id.switch_e:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[4]);
                switchLabel = "E";
                break;
            case R.id.switch_f:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[5]);
                switchLabel = "F";
                break;
            case R.id.switch_g:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[6]);
                switchLabel = "G";
                break;
            case R.id.switch_h:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[7]);
                switchLabel = "H";
                break;
            case R.id.switch_i:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[8]);
                switchLabel = "I";
                break;
            case R.id.switch_j:
                pressSwitch(mBoardStatus, SWITCH_SQUARES[9]);
                switchLabel = "J";
                break;
            default:
                break;
        }

        visualizeBoard();

        mMoveCount += 1;
        mMoveCountTextView.setText(String.valueOf(mMoveCount));

        if (isValidSolution(mBoardStatus)) {
            if (mMoveCount == mOptimalSolution.length()) {
                mToast = Toast.makeText(MainActivity.this,
                        "Winning with optimal solution!!!\nPress RESTART button to continue.",
                        Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(MainActivity.this,
                        "Winning! Possible shorter sequence is:" + mOptimalSolution + "\nPress RESTART button to continue.",
                        Toast.LENGTH_SHORT);
            }
            mToast.show();
        } else {
            // Game is not over yet
            mMoveHistoryTextView.append(switchLabel);
        }
    }

    public void onClickRestart(View view) {
        if (mToast != null) {
            mToast.cancel();
        }
        randomInitializeBoard();
        visualizeBoard();
        mMoveCount = 0;
        mMoveCountTextView.setText(String.valueOf(mMoveCount));
        mMoveHistoryTextView.setText("");
        mToast = Toast.makeText(MainActivity.this, "Restart new game!", Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void onClickRandomRestart(View view) {
        if (mToast != null) {
            mToast.cancel();
        }
        Random r = new Random();
        mOptimalSolution = "";

        // Binary representation of the board. Each square is represented by one bit.
        // Bit set to 1 means whilte. So all white is represented as 0xFFFF, all black as 0x0000.
        // A random board is pickek from [0x0000, 0x10000).
        int randomBitBoard = r.nextInt(0x10000);
        for (int squareId = 0; squareId < 16; ++squareId) {
            if ((randomBitBoard & (1 << squareId)) != 0) {
                mBoardStatus[squareId] = SquareStatus.WHITE;
                mBoardSquares[squareId].setImageResource(R.drawable.white);
            } else {
                mBoardStatus[squareId] = SquareStatus.BLACK;
                mBoardSquares[squareId].setImageResource(R.drawable.black);
            }
        }

        visualizeBoard();
        mMoveCount = 0;
        mMoveCountTextView.setText(String.valueOf(mMoveCount));
        mMoveHistoryTextView.setText("");
        mToast = Toast.makeText(MainActivity.this, "Restart new random game!", Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void onClickAuto(View view) {
        if (mToast != null) {
            mToast.cancel();
        }
        int optimalSolution = autoSolve();
        int optimalSolutionLength = 0;

        for (int bitIndex = 0; bitIndex < 10; ++bitIndex) {
            if ((optimalSolution & (1 << bitIndex)) != 0) {
                optimalSolutionLength += 1;
            }
        }

        if (optimalSolution == -1) {
            mToast = Toast.makeText(MainActivity.this, "Cannot find solution!", Toast.LENGTH_SHORT);
            mToast.show();
            return;
        } else {
            mToast = Toast.makeText(MainActivity.this, "Found solution with " + String.valueOf(optimalSolutionLength) + " moves.", Toast.LENGTH_SHORT);
            mToast.show();
        }

        int totalMove = 0;
        for (int bitIndex = 0; bitIndex < 10; ++bitIndex) {
            if ((optimalSolution & (1 << bitIndex)) != 0) {
                Animation buttonShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                totalMove += 1;

                final int finalSwitchIndex = bitIndex;
                buttonShake.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        for (final int squareId : SWITCH_SQUARES[finalSwitchIndex]) {
                            final SquareStatus originalStatus = mBoardStatus[squareId];
                            Drawable backgrounds[] = new Drawable[2];
                            Resources res = getResources();
                            if (originalStatus == SquareStatus.BLACK) {
                                mBoardStatus[squareId] = SquareStatus.WHITE;
                                backgrounds[0] = ResourcesCompat.getDrawable(res, R.drawable.black, null);
                                backgrounds[1] = ResourcesCompat.getDrawable(res, R.drawable.white, null);
                            } else {
                                mBoardStatus[squareId] = SquareStatus.BLACK;
                                backgrounds[0] = ResourcesCompat.getDrawable(res, R.drawable.white, null);
                                backgrounds[1] = ResourcesCompat.getDrawable(res, R.drawable.black, null);
                            }
                            TransitionDrawable crossFader = new TransitionDrawable(backgrounds);
                            mBoardSquares[squareId].setImageDrawable(crossFader);

                            crossFader.startTransition(ANIMATION_TRANSITION_TIME);
                        }
                        mMoveCount += 1;
                        mMoveCountTextView.setText(String.valueOf(mMoveCount));
                        mMoveHistoryTextView.append(String.valueOf((char) ('A' + finalSwitchIndex)));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                buttonShake.setStartOffset(ANIMATION_TRANSITION_TIME * 5 * totalMove);
                mSwitchButton[bitIndex].startAnimation(buttonShake);
            }
        }
    }

    private void showManualEditInterface() {
        mBoardGridLayout.setVisibility(View.VISIBLE);

        mSwitchGridLayout.setVisibility(View.INVISIBLE);
        mControlGridlLayout.setVisibility(View.INVISIBLE);
        mInfoLinearLayout.setVisibility(View.INVISIBLE);
        mManualSetDoneButton.setVisibility(View.VISIBLE);
    }

    private void hideManualEditInterface() {
        mBoardGridLayout.setVisibility(View.VISIBLE);

        mSwitchGridLayout.setVisibility(View.VISIBLE);
        mControlGridlLayout.setVisibility(View.VISIBLE);
        mInfoLinearLayout.setVisibility(View.VISIBLE);
        mManualSetDoneButton.setVisibility(View.INVISIBLE);
    }

    // Flip one square, used for manual edit only.
    private void flipSquare(int squareId) {
        if (mBoardStatus[squareId] == SquareStatus.BLACK) {
            mBoardStatus[squareId] = SquareStatus.WHITE;
            mBoardSquares[squareId].setImageResource(R.drawable.white);
        } else {
            mBoardStatus[squareId] = SquareStatus.BLACK;
            mBoardSquares[squareId].setImageResource(R.drawable.black);
        }
    }

    private void visualizeBoard() {
        for (int squareId = 0; squareId < 16; ++squareId) {
            if (mBoardStatus[squareId] == SquareStatus.BLACK) {
                mBoardSquares[squareId].setImageResource(R.drawable.black);
            } else {
                mBoardSquares[squareId].setImageResource(R.drawable.white);
            }
        }
    }

    private boolean isValidSolution(final SquareStatus[] boardStatus) {
        int totalSum = 0;
        for (SquareStatus ele : boardStatus) {
            totalSum += ele.getValue();
        }

        return totalSum == ALL_WHITE_BOARD || totalSum == ALL_BLACK_BOARD;
    }

    private int autoSolve() {
        int bestSolutionLength = Integer.MAX_VALUE;
        int bestSolution = 0;
        for (int solution = 0; solution < 1024; ++solution) {
            SquareStatus[] squareStatuses = mBoardStatus.clone();
            int solutionLength = 0;
            for (int bitIndex = 0; bitIndex < 10; ++bitIndex) {
                if ((solution & (1 << bitIndex)) != 0) {
                    pressSwitch(squareStatuses, SWITCH_SQUARES[bitIndex]);
                    solutionLength += 1;
                }
            }

            if (isValidSolution(squareStatuses)) {
                if (solutionLength < bestSolutionLength) {
                    bestSolutionLength = solutionLength;
                    bestSolution = solution;
                }
            }
        }

        if (bestSolutionLength <= 10) {
            // extract the solution from
            return bestSolution;
        } else {
            // no solution found
            return -1;
        }
    }

    private void randomInitializeBoard() {
        // For a valid board configuration, each switch can be pressed at most once.
        // So we generate valid configuration by randomly sample the probability of each key showing
        // up in the final result. Assume each key obeys the [0, 1) uniform distribution.
        // We select a key only if the sampled probability is larger than 0.5.
        //
        // It's also trivial to get the optimal solution this way.
        // Notice that the order doesn't matter.

        Random r = new Random();
        mOptimalSolution = "";

        // We first randomly set the entire board to ALL_WHITE or ALL_BLACK state
        double setToWhiteProbability = r.nextDouble();
        SquareStatus initialStatus;
        if (setToWhiteProbability > 0.5) {
            initialStatus = SquareStatus.WHITE;
        } else {
            initialStatus = SquareStatus.BLACK;
        }
        for (int squareIdx = 0; squareIdx < 16; ++squareIdx) {
            mBoardStatus[squareIdx] = initialStatus;
        }

        for (int switchId = 0; switchId < 10; ++switchId) {
            double sampleProbability = r.nextDouble();
            if (sampleProbability > 0.5) {
                pressSwitch(mBoardStatus, SWITCH_SQUARES[switchId]);
                mOptimalSolution += (char) (switchId + 'A');
            }
        }
        mMoveCount = 0;
        mMoveCountTextView.setText(String.valueOf(mMoveCount));
        mMoveHistoryTextView.setText("");
    }

    private void pressSwitch(final SquareStatus[] boardStatus, final int[] squareIds) {
        for (int squareId : squareIds) {
            if (boardStatus[squareId] == SquareStatus.WHITE) {
                boardStatus[squareId] = SquareStatus.BLACK;
            } else {
                boardStatus[squareId] = SquareStatus.WHITE;
            }
        }
    }
}
