
class Solution {
    public long countNonDecreasingSubarrays(int[] nums, int k) {
        int n = nums.length;

        int LOG = 1;
        while ((1 << LOG) <= n) LOG++;

        int[] next = new int[n + 1];
        int[] stack = new int[n];
        int top = 0;

        // Step 1: Find next greater element
        for (int i = n - 1; i >= 0; i--) {
            while (top > 0 && nums[stack[top - 1]] <= nums[i]) {
                top--;
            }

            next[i] = (top == 0) ? n : stack[top - 1];
            stack[top++] = i;
        }

        next[n] = n;

        // Step 2: Binary lifting tables
        int[][] up = new int[LOG][n + 1];
        long[][] sum = new long[LOG][n + 1];

        for (int i = 0; i <= n; i++) {
            up[0][i] = next[i];

            if (i < n) {
                sum[0][i] = (long) nums[i] * (next[i] - i);
            }
        }

        for (int p = 1; p < LOG; p++) {
            for (int i = 0; i <= n; i++) {
                int mid = up[p - 1][i];

                up[p][i] = up[p - 1][mid];

                sum[p][i] = sum[p - 1][i]
                         + sum[p - 1][mid];
            }
        }

        // Step 3: Prefix sums
        long[] prefix = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        // Step 4: Calculate required operations
        long ans = 0;

        for (int r = 0; r < n; r++) {
            int low = 0;
            int high = r;

            while (low < high) {
                int mid = low + (high - low) / 2;

                long operations =
                    getCost(mid, r, nums, up, sum, prefix, LOG);

                if (operations <= k) {
                    high = mid;
                } else {
                    low = mid + 1;
                }
            }

            ans += r - low + 1;
        }

        return ans;
    }

    private long getCost(
        int l,
        int r,
        int[] nums,
        int[][] up,
        long[][] sum,
        long[] prefix,
        int LOG
    ) {
        int cur = l;
        long maxSum = 0;

        // Calculate sum of prefix maximums
        for (int p = LOG - 1; p >= 0; p--) {
            int nxt = up[p][cur];

            if (nxt <= r) {
                maxSum += sum[p][cur];
                cur = nxt;
            }
        }

        maxSum += (long) nums[cur] * (r - cur + 1);

        // Required increments
        return maxSum - (prefix[r + 1] - prefix[l]);
    }
}
